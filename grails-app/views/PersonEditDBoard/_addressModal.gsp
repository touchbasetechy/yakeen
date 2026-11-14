<!-- Modal -->
<div class="modal fade" id="statusModal" tabindex="-1" aria-labelledby="statusModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered modal-lg">
    <div class="modal-content">
      <!-- Header -->
      <div class="modal-header border-0">
        <h5 class="modal-title" id="statusModalLabel">Available Address</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
          aria-hidden="true">&times;</span>
        </button>
      </div>

      <!-- Body -->
      <div class="modal-body border-0">
        <table>
          <thead>
          <tr>
            <th>Address Details</th>
            <th></th>
          </tr>
          </thead>
          <tbody>
          <tr>
            <td>Street1</td>
            <td>
              ${flash?.address?.street1}
            </td>
          </tr>
          <tr>
            <td>Street2</td>
            <td>
              ${flash?.address?.street2}
            </td>
          </tr>
          <tr>
            <td>City</td>
            <td>
              ${flash?.address?.city}
            </td>
          </tr>
          <tr>
            <td>District</td>
            <td>
              ${flash?.address?.district}
            </td>
          </tr>
          <tr>
            <td>State</td>
            <td>
              ${flash?.address?.state}
            </td>
          </tr>
          <tr>
            <td>Country</td>
            <td>
              ${flash?.address?.country}
            </td>
          </tr>
          <tr>
            <td>Postal Code</td>
            <td>
              ${flash?.address?.postalCode}
            </td>
          </tr>
          <tr>
            <td>Address Type</td>
            <td>
              ${flash?.address?.addressType}
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- Footer -->
      <div class="modal-footer border-0">
      </div>
    </div>
  </div>
</div>
