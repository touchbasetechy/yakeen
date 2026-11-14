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

<!-- ── Ticket type table ── -->
  <g:if test="${ticketTypes}">
    <glass:listTable id="ticketTypeTable"
                     cols="Ticket Type, Description, Status"
                     caption="Available Ticket Types"
                     searchable="true"
                     emptyText="No ticket types match your search"
                     backAction="listHobbiesToAdd"
                     backController="DBoard"
                     backIcon="fas fa-chevron-left"
                     homeAction="index"
                     homeController="DBoard"
                     homeIcon="fas fa-home"
                     rowController="DBoard"
                     rowAction="addTicketForAddTicket"
                     rowParams="${[ac: 'an', slot: slot]}"
                     moreController="DBoard"
                     moreAction="listMoreTicketTypes"
                     moreParams="${[slot: slot]}"
                     fields="name, description"
                     badgeText="Available"
                     badgeVariant="violet"
                     loadingText="Loading more ticket types…"
                     max="10">
      <g:each in="${ticketTypes}" var="ticketType">
        <tr data-row-id="${ticketType?.id}">
          <td>${ticketType?.name?.encodeAsHTML()}</td>
          <td>${ticketType?.description?.encodeAsHTML()}</td>
          <td>
            <glass:tableBadge text="Available" variant="violet" dot="true"/>
          </td>
        </tr>
      </g:each>
    </glass:listTable>
  </g:if>
  <g:else>
    <glass:table id="ticketTypeTable"
                 cols="Ticket Type, Status, Actions"
                 caption="Available Ticket Types"
                 searchable="false"
                 emptyText="No ticket types found"
                 backAction="listHobbiesToAdd"
                 backController="DBoard"
                 backIcon="fas fa-chevron-left"
                 homeAction="index"
                 homeController="DBoard"
                 homeIcon="fas fa-home">
      <tr>
        <td>No ticket types found.</td>
        <td>
          <glass:tableBadge text="Unavailable" variant="danger" dot="true"/>
        </td>
        <td>
          <glass:tableActions>
            <glass:tableActionBtn
              icon="fa-solid fa-plus"
              controller="DBoard"
              action="addTicket"
              variant="success"
              title="Add Ticket Type"/>
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

    /* ── Pre-select the row given by ?at=…&t={ticketTypeId} ──
       Ticks it and scrolls to it; if it is not in the first page, keeps
       triggering the table's infinite scroll until the row appears. */
    var params = new URLSearchParams(window.location.search);
    var target = params.get('t');
    var tbl = document.getElementById('ticketTypeTable');
    if (!params.get('at') || !target || !tbl) return;

    var tbody = tbl.querySelector('tbody');
    var found = false;

    function selectTarget() {
      if (found) return true;
      var row = tbody.querySelector('tr[data-row-id="' + String(target).replace(/"/g, '\\"') + '"]');
      if (!row) return false;
      found = true;
      if (tbl.gtDrawTick) tbl.gtDrawTick(row, -1);
      row.scrollIntoView({behavior: 'smooth', block: 'center'});
      return true;
    }

    function nudge() {
      window.scrollTo(0, document.body.scrollHeight);
    }

    if (selectTarget()) return;

    var observer = new MutationObserver(function () {
      if (selectTarget()) observer.disconnect();
      else nudge();
    });
    observer.observe(tbody, {childList: true});
    nudge();
  })();
</script>
</body>
</html>
