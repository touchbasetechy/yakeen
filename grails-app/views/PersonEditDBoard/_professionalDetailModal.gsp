<!-- Modal -->
<div class="modal fade" id="statusModal" tabindex="-1" aria-labelledby="statusModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered modal-lg">
    <div class="modal-content">
      <!-- Header -->
      <div class="modal-header">
        <h5 class="modal-title" id="statusModalLabel">Available Current Jobs</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
          aria-hidden="true">&times;</span>
        </button>
      </div>

      <!-- Body -->
      <div class="modal-body">
        <table>
          <thead>
          <tr>
            <th>Job Details</th>
            <th></th>
          </tr>
          </thead>
          <tbody>
          <tr>
            <td>Designation</td>
            <td>
              ${flash?.profession?.designation}
            </td>
          </tr>
          <tr>
            <td>Organisation</td>
            <td>
              ${flash?.profession?.organisation}
            </td>
          </tr>
          <tr>
            <td>Organisation Location</td>
            <td>
              ${flash?.profession?.organisationLocation}
            </td>
          </tr>
          <tr>
            <td>Job Category</td>
            <td>
              ${flash?.profession?.jobCategory}
            </td>
          </tr>
          <tr>
            <td>Is Current Job</td>
            <td>
              <g:if test="${flash?.profession?.isCurrent}">
                <div class="success-msg">
                  Yes
                </div>
              </g:if>
              <g:else>
                <div class="error-msg">
                  No
                </div>
              </g:else>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- Footer -->
      <div class="modal-footer">
      </div>
    </div>
  </div>
</div>
