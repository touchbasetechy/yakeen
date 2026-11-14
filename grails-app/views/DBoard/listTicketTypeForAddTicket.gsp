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

  /* ── Table container ── */
  .table-wrap {
    width: 100%;
    max-width: 920px;
  }

  /* ── Row click cursor ── */
  .gt-table tbody tr {
    cursor: pointer;
  }

  /* ── Selected row highlight ── */
  .gt-table tbody tr.row-selected td {
    background: rgba(219, 39, 119, .12) !important;
  }

  /* ── Tick SVG inside cell ── */
  .tick-svg {
    width: 22px;
    height: 22px;
    display: inline-block;
    vertical-align: middle;
  }

  /* ── Loading indicator ── */
  .gt-loading {
    display: none;
    align-items: center;
    gap: 10px;
    padding: 14px 20px;
    border-radius: 12px;
    background: rgba(255, 255, 255, .05);
    border: 1px solid rgba(255, 150, 180, .12);
    color: rgba(255, 180, 200, .55);
    font-size: 12px;
    font-family: 'DM Mono', monospace;
    letter-spacing: .08em;
  }

  .gt-loading i {
    animation: spin 1s linear infinite;
  }

  @keyframes spin {
    to {
      transform: rotate(360deg);
    }
  }
  </style>
</head>

<body>
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

<!-- ── Glass table ── -->
  <g:if test="${ticketTypes}">
    <glass:listTable id="ticketTypeTable"
                     cols="Ticket Type, Description, Status"
                     caption="Select Ticket Type"
                     searchable="true"
                     emptyText="No ticket types match your search"
                     backAction="index"
                     backController="DBoard"
                     backIcon="fas fa-chevron-left"
                     homeAction="index"
                     homeController="DBoard"
                     homeIcon="fas fa-home"
                     rowTickCol="2">

      <g:each in="${ticketTypes}" var="ticketType">
        <tr data-ticket-type-id="${ticketType?.id}"
            data-ticket-type-name="${ticketType?.name?.encodeAsHTML()}">
          <td>${ticketType?.name?.encodeAsHTML()}</td>
          <td>${ticketType?.description?.encodeAsHTML()}</td>
          <td>
            <glass:tableBadge text="Available" variant="violet" dot="true"/>
          </td>
        </tr>
      </g:each>
    </glass:listTable>

    <!-- Loading indicator -->
    <div id="loading-indicator" class="gt-loading" style="margin-top: 12px;">
      <i class="fa-solid fa-spinner"></i>
      <span>Loading more ticket types&hellip;</span>
    </div>

  </g:if>
  <g:else>
    <glass:table id="ticketTypeTable"
                 cols="Ticket Type, Status, Actions"
                 caption="Select Ticket Type"
                 searchable="false"
                 emptyText="No ticket types found"
                 backAction="index"
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
              href="${createLink(controller: 'DBoard', action: 'addDummyTicket')}"
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

    /* ── Infinite scroll state ── */
    var offset = 10, max = 10, loading = false, exhausted = false;
    var tableBody = document.querySelector('#ticketTypeTable tbody');
    var loadingEl = document.getElementById('loading-indicator');
    var counter = document.querySelectorAll('#ticketTypeTable tbody tr').length;

    /* ── URL params (for pre-selection on load) ── */
    var urlParams = new URLSearchParams(window.location.search);
    var rowSelected = false;

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

    /* ── Row click → navigate ── */
    function bindRow(row) {
      row.addEventListener('click', function () {
        var ticketTypeId = row.getAttribute('data-ticket-type-id');
        if (!ticketTypeId) {
          return;
        }

        drawTick(row);

        var url = "${createLink(controller: 'DBoard', action: 'addTicketForAddTicket', params: [id: 'REPLACE_ID', ac: 'REPLACE_AC', slot: "${request.getAttribute('currentSlot')}"])}";
        url = url.replace('REPLACE_ID', ticketTypeId).replace('REPLACE_AC', 'an');

        setTimeout(function () {
          window.location.href = url;
        }, 280);
      });
    }

    /* ── Tick SVG ── */
    function drawTick(row) {
      row.classList.add('row-selected');
      var statusCell = row.cells[2];
      if (!statusCell) {
        return;
      }

      statusCell.innerHTML = '';
      var svgNS = 'http://www.w3.org/2000/svg';
      var svg = document.createElementNS(svgNS, 'svg');
      svg.setAttribute('viewBox', '0 0 75.2 75.2');
      svg.setAttribute('class', 'tick-svg');

      var circle = document.createElementNS(svgNS, 'circle');
      circle.setAttribute('fill', 'none');
      circle.setAttribute('stroke', '#34d399');
      circle.setAttribute('stroke-width', '3');
      circle.setAttribute('stroke-miterlimit', '10');
      circle.setAttribute('cx', '35.1');
      circle.setAttribute('cy', '35.1');
      circle.setAttribute('r', '30.0');

      var polyline = document.createElementNS(svgNS, 'polyline');
      polyline.setAttribute('fill', 'none');
      polyline.setAttribute('stroke', '#34d399');
      polyline.setAttribute('stroke-width', '3');
      polyline.setAttribute('stroke-linecap', 'round');
      polyline.setAttribute('stroke-miterlimit', '10');
      polyline.setAttribute('points', '20,37 30,47 55,22');

      svg.appendChild(circle);
      svg.appendChild(polyline);
      statusCell.appendChild(svg);
    }

    /* ── Bind initial rows + handle pre-selection ── */
    document.querySelectorAll('#ticketTypeTable tbody tr').forEach(function (row) {
      var ticketTypeId = row.getAttribute('data-ticket-type-id');
      if (urlParams.get('at') && ticketTypeId && ticketTypeId === urlParams.get('t')) {
        drawTick(row);
        rowSelected = true;
        row.scrollIntoView({behavior: 'smooth', block: 'center'});
      }
      bindRow(row);
    });

    /* If pre-selected row not in initial set, load more to find it */
    if (urlParams.get('at') && !rowSelected) {
      loadMoreData();
    }

    /* ── Infinite scroll ── */
    function isNearBottom() {
      return (window.innerHeight + window.scrollY) >= document.body.offsetHeight - 200;
    }

    function loadMoreData() {
      if (loading || exhausted) {
        return;
      }
      loading = true;
      if (loadingEl) {
        loadingEl.style.display = 'flex';
      }

      var baseUrl = "${createLink(controller: 'DBoard', action: 'listMoreTicketTypes', params: [slot: "${request.getAttribute('currentSlot')}"])}";
      var slot = "${request.getAttribute('currentSlot') ?: ''}";
      var url = baseUrl + "?offset=" + offset + "&max=" + max + "&slot=" + encodeURIComponent(slot);

      fetch(url)
        .then(function (r) {
          return r.json();
        })
        .then(function (data) {
          if (!data || data.length === 0) {
            exhausted = true;
          } else {
            data.forEach(function (ticketType) {
              var tr = document.createElement('tr');
              tr.setAttribute('data-ticket-type-id', ticketType.id);
              tr.setAttribute('data-ticket-type-name', ticketType.name || '');

              var td1 = document.createElement('td');
              td1.textContent = ticketType.name || '';

              var td2 = document.createElement('td');
              td2.textContent = ticketType.description || '';

              var td3 = document.createElement('td');
              td3.innerHTML = '<span class="gt-badge gt-badge-violet"><span class="gt-badge-dot"></span>Available</span>';

              tr.appendChild(td1);
              tr.appendChild(td2);
              tr.appendChild(td3);

              tr.style.animationDelay = (counter * 45) + 'ms';
              counter++;

              tableBody.appendChild(tr);

              /* Handle pre-selection in lazily loaded rows */
              var id = tr.getAttribute('data-ticket-type-id');
              if (urlParams.get('at') && id && id === urlParams.get('t')) {
                drawTick(tr);
                rowSelected = true;
                tr.scrollIntoView({behavior: 'smooth', block: 'center'});
              }

              bindRow(tr);
            });

            offset += max;

            /* Keep loading until pre-selected row is found */
            if (urlParams.get('at') && !rowSelected) {
              loadMoreData();
            }
          }
        })
        .catch(function (err) {
          console.error('Load error:', err);
        })
        .finally(function () {
          loading = false;
          if (loadingEl) {
            loadingEl.style.display = 'none';
          }
        });
    }

    /* Trigger load on scroll */
    window.addEventListener('scroll', function () {
      if (isNearBottom()) {
        loadMoreData();
      }
    });

  })();
</script>
</body>
</html>
