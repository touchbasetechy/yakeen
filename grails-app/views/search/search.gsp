<!doctype html>
<html>
<head>
  <meta name="layout" content="search"/>
  <glass:tableStyles/>
</head>

<body>
<a href="${createLink(controller: 'search', action: 'index')}" class="same-line"><i
  class="fas fa-chevron-left embossed-link" aria-hidden="true"></i></a>

<h4 class="same-line">Search Results</h4>

<section class="row colset-2-its">
  <div class="container">

    <glass:tabs id="searchResultTabs" variant="pills" accent="#a78bfa" isSemiTransparent="true">

      <glass:tab title="List" icon="fa-solid fa-list" active="true">
        <glass:table
          cols="First Name, Last Name, EMail Id, Gender, Religion, Caste, Smoking Habit, Drinking Habit"
          id="searchResultsTable"
          caption="${searchResult.size()} match${searchResult.size() == 1 ? '' : 'es'}"
          emptyText="No matches found"
          rowController="spouseprofile"
          rowAction="readSpouseProfile"
          rowIdAttr="data-person-id"
          rowTickCol="-1">

          <g:each var="person" in="${searchResult}">
            <tr data-person-id="${person[0]}">
              <td>${person[1]}</td>
              <td>${person[2]}</td>
              <td>${person[3]}</td>
              <td>${person[4]}</td>
              <td>${person[5]}</td>
              <td>${person[6]}</td>
              <td>${person[7]}</td>
              <td>${person[8]}</td>
            </tr>
          </g:each>

        </glass:table>
      </glass:tab>

      <glass:tab title="Grid" icon="fa-solid fa-table-cells-large">
        <div class="flex-container">
          <g:if test="${searchResult.size() > 0}">
            <g:each var="person" in="${searchResult}">
              <glass2:userCard id="${person[0]}" firstName="${person[1]}" lastName="${person[2]}"
                               email="${person[3]}" gender="${person[4]}"/>
            </g:each>
          </g:if>
          <g:else>
            <div class="card flex-item border-0" style="width: 18rem;">
              <img src="${resource(dir: 'images', file: 'Male-no-photo.png')}" class="card-img-top"
                   alt="Profile Image">

              <div class="card-body">
                No matches found
              </div>
            </div>
          </g:else>
        </div>
      </glass:tab>

    </glass:tabs>

  </div>
</section>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {
    var tabNav = document.getElementById('searchResultTabs');
    if (!tabNav) return;

    var STORAGE_KEY = 'selectedTab_searchResultTabs';

    tabNav.querySelectorAll('button[data-bs-toggle="tab"]').forEach(function (btn) {
      btn.addEventListener('shown.bs.tab', function (e) {
        var target = e.target.getAttribute('data-bs-target');
        if (target) localStorage.setItem(STORAGE_KEY, target);
      });
    });

    var selectedTab = localStorage.getItem(STORAGE_KEY);
    var toActivate = selectedTab
      ? tabNav.querySelector('button[data-bs-target="' + selectedTab + '"]')
      : null;

    if (!toActivate) {
      toActivate = tabNav.querySelector('button[data-bs-toggle="tab"]');
    }

    if (toActivate) {
      var tab = bootstrap.Tab.getOrCreateInstance(toActivate);
      tab.show();
    }
  });
</script>

</body>
</html>
