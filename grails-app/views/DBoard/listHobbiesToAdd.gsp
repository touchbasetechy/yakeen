<!doctype html>
<html>
<head>
  <meta name="layout" content="dboardList"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <glass:tableStyles/>
  <style>
  .page-wrap {
    position: relative;
    z-index: 1;
    padding: 40px 28px 80px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 32px;
  }

  /* ── Flash alerts — rose-tinted ── */
  .gt-alert {
    width: 100%;
    max-width: 920px;
    padding: 13px 18px;
    border-radius: 14px;
    backdrop-filter: blur(16px);
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 13px;
    font-weight: 300;
  }

  .gt-alert-error {
    background: rgba(159, 18, 57, .18);
    border: 1px solid rgba(255, 100, 150, .25);
    color: rgba(255, 200, 215, .85);
  }

  .gt-alert-success {
    background: rgba(52, 211, 153, .10);
    border: 1px solid rgba(52, 211, 153, .28);
    color: rgba(167, 243, 208, .85);
  }

  .gt-alert i {
    font-size: 13px;
    flex-shrink: 0;
  }
  </style>
</head>

<body>
<g:set var="slot" value="${request.getAttribute('currentSlot') ?: ''}"/>

<div class="page-wrap">

<!-- ── Flash messages ── -->
  <g:if test="${flash.error}">
    <div class="gt-alert gt-alert-error">
      <i class="fa-solid fa-circle-exclamation"></i>
      <span>${flash.error}</span>
    </div>
  </g:if>
  <g:if test="${flash.message}">
    <div class="gt-alert gt-alert-success">
      <i class="fa-solid fa-circle-check"></i>
      <span>${flash.message}</span>
    </div>
  </g:if>

<!-- ── Hobby table ── -->
  <g:if test="${hobbies}">
    <glass:listTable id="hobbyTable"
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
                     moreAction="listMoreHobbies"
                     moreParams="${[slot: slot]}"
                     fields="name, description"
                     badgeText="Available"
                     badgeVariant="violet"
                     loadingText="Loading more hobbies…"
                     max="10">
      <g:each in="${hobbies}" var="hobby">
        <tr data-row-id="${hobby?.id}">
          <td>${hobby?.name?.encodeAsHTML()}</td>
          <td>${hobby?.description?.encodeAsHTML()}</td>
          <td>
            <glass:tableBadge text="Available" variant="violet" dot="true"/>
          </td>
        </tr>
      </g:each>
    </glass:listTable>
  </g:if>
  <g:else>
    <glass:table id="hobbyTable"
                 cols="Hobby, Status, Actions"
                 caption="Available Hobbies"
                 searchable="false"
                 emptyText="No hobbies found"
                 homeAction="index"
                 homeController="DBoard"
                 homeIcon="fas fa-chevron-left">
      <tr>
        <td>No hobbies found.</td>
        <td>
          <glass:tableBadge text="Unavailable" variant="danger" dot="true"/>
        </td>
        <td>
          <glass:tableActions>
            <glass:tableActionBtn
              icon="fa-solid fa-plus"
              href="${createLink(controller: 'DBoard', action: 'listTicketTypeForAdd', params: [slot: "${request.getAttribute('currentSlot')}"])}"
              variant="success"
              title="Add Hobby"/>
          </glass:tableActions>
        </td>
      </tr>
    </glass:table>
  </g:else>

</div><!-- /.page-wrap -->

<script type="text/javascript">
  (function () {
    /* ── Flash auto-hide ── */
    document.querySelectorAll('.gt-alert').forEach(function (el) {
      setTimeout(function () {
        el.style.transition = 'opacity .6s';
        el.style.opacity = '0';
        setTimeout(function () {
          el.remove();
        }, 600);
      }, 6000);
    });
  })();
</script>
</body>
</html>
