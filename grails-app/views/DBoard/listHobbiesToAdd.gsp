<!doctype html>
<html>
<head>
  <meta name="layout" content="dboardList"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <glass:tableStyles/>
</head>

<body>
<%-- toString() first: a slot of Integer 0 is falsy in Groovy, so "0 ?: ''" would turn slot 0 into '' --%>
<g:set var="slot" value="${request.getAttribute('currentSlot')?.toString() ?: ''}"/>
<g:set var="ctxPath" value="${request.contextPath ?: ''}"/>
<g:set var="listBase" value="${createLink(controller: 'hobby', action: 'listHobbiesForCategory').toString()}"/>
<%-- slot-aware endpoint, built like glass:table does: /u/{slot}/hobby/listHobbiesForCategory --%>
<g:set var="listUrl"
       value="${slot && !listBase.startsWith(ctxPath + '/u/' + slot + '/') ? ctxPath + '/u/' + java.net.URLEncoder.encode(slot, 'UTF-8') + listBase.substring(ctxPath.length()) : listBase}"/>
<g:set var="tableHeader">
  <glass:select
    name="hobbyCategories" id="hobbyCategories" label="Hobby Categories"
    from="${hobbyCategories}"
    value="${selectedCategory}"
    optionKey="name" optionValue="name"
    multiple="false" required="true" disabled="false"/>
</g:set>

<div class="page-wrap">
  <%-- fields map 1:1 to cols: Hobby -> name, Description -> description (Status is the badge) --%>
  <glass:listTable id="hobbyTable"
                   headerHtml="${tableHeader}"
                   cols="Hobby, Description, Status"
                   caption="Available Hobbies"
                   searchable="true"
                   emptyText="No hobbies match your search"
                   homeAction="index"
                   homeController="DBoard"
                   homeIcon="fas fa-chevron-left"
                   rowController="Hobby"
                   rowAction="addHobbyToUser"
                   rowParams="${[ac: 'an', slot: slot]}"
                   moreController="Hobby"
                   moreAction="listHobbiesForCategory"
                   moreMethod="POST"
                   fields="name, description"
                   idField="id"
                   max="10"
                   badgeText="Available"
                   badgeVariant="violet"
                   loadingText="Loading more hobbies…">
  </glass:listTable>

</div><!-- /.page-wrap -->

<script type="text/javascript">
  (function () {
    /* ── Hobby category select -> reload the hobby table ── */
    var tbl = document.getElementById('hobbyTable');
    var select = document.getElementById('hobbyCategories');
    var PAGE_SIZE = 10;
    var LIST_URL = '${raw(listUrl)}';

    if (!tbl || !select) {
      console.warn('Hobby table or hobby categories not found');
      return;
    }

    // option value (category name) -> category id; used in case the select's values are names
    var CATEGORY_IDS = ${raw((hobbyCategories.collectEntries { [(it.name): it.id] } as grails.converters.JSON).toString())};

    var inflight = null;

    function categoryIdOf(value) {
      if (CATEGORY_IDS[value]) return CATEGORY_IDS[value];
      return value !== '' && !isNaN(value) ? Number(value) : null;   // value is already an id
    }

    // Send JSON (Content-Type) and ask for JSON back (Accept); add the CSRF header when the layout provides one.
    function jsonHeaders() {
      var h = {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
      };
      var token = document.querySelector('meta[name="_csrf"]');
      var header = document.querySelector('meta[name="_csrf_header"]');

      if (token && header) h[header.content] = token.content;
      return h;
    }

    // Make sure every row carries a description so the column never renders "undefined"/"null".
    function withDescriptions(hobbies) {
      return hobbies.map(function (h) {
        if (h.description == null) h.description = '';
        return h;
      });
    }

    // Removes the option of a category that has no hobbies left and moves on to the next one.
    function removeOption(value) {
      Array.prototype.slice.call(select.options).forEach(function (o) {
        if (o.value === value) o.parentNode.removeChild(o);
      });
      delete CATEGORY_IDS[value];
      if (select.options.length > 0) {
        select.selectedIndex = 0;
        loadCategory();
      } else {
        tbl.gtReplaceData([]);                 // nothing left to show
      }
    }

    function loadCategory() {
      var value = select.value;
      var categoryId = categoryIdOf(value);
      if (!categoryId) return;

      if (inflight) inflight.abort();
      inflight = new AbortController();

      // Scroll-loading in glass:listTable sends gtHeaderState with every next-page request,
      // so appended pages come from the selected category too.
      tbl.gtHeaderState = {hobbyCategoryId: categoryId};
      tbl.dispatchEvent(new CustomEvent('gt:headerReload', {detail: {params: tbl.gtHeaderState}}));

      fetch(LIST_URL, {
        method: 'POST',
        credentials: 'same-origin',
        headers: jsonHeaders(),
        body: JSON.stringify({hobbyCategoryId: categoryId, offset: 0, max: PAGE_SIZE}),
        signal: inflight.signal
      })
        .then(function (res) {
          return res.json().then(function (data) {
            if (!res.ok) throw new Error(data && data.message ? data.message : 'HTTP ' + res.status);
            return data;
          });
        })
        .then(function (data) {
          if (!data.hobbies || data.hobbies.length === 0) {
            removeOption(value);
            return;
          }
          tbl.gtReplaceData(withDescriptions(data.hobbies));   // clears the table and loads the new set
        })
        .catch(function (err) {
          if (err.name === 'AbortError') return;
          console.error('List hobbies for category failed:', err);
        });
    }

    // the first page was rendered for the selected category
    var initialId = ${selectedCategory?.id ?: 'null'};
    if (initialId) tbl.gtHeaderState = {hobbyCategoryId: initialId};

    select.addEventListener('change', loadCategory);
  })();
</script>
</body>
</html>
