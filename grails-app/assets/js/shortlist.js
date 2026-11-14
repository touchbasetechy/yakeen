// grails-app/assets/javascripts/shortlist.js

$(document).ready(function () {

  // Add to shortlist
  $(document).on('click', '.add-to-shortlist', function () {
    var userId = $(this).data('user-id');
    var button = $(this);
    var notes = button.closest('.profile-card').find('.shortlist-notes-input').val() || '';

    button.prop('disabled', true).text('Adding...');

    $.ajax({
      url: '/shortlist/add',
      type: 'POST',
      data: {
        userId: userId,
        notes: notes
      },
      success: function (response) {
        if (response.success) {
          button.removeClass('btn-success add-to-shortlist')
            .addClass('btn-danger remove-shortlist')
            .text('Remove from Shortlist');

          // Update shortlist count if element exists
          updateShortlistCount();

          showMessage(response.message, 'success');
        } else {
          showMessage(response.message, 'error');
          button.prop('disabled', false).text('Add to Shortlist');
        }
      },
      error: function () {
        showMessage('An error occurred. Please try again.', 'error');
        button.prop('disabled', false).text('Add to Shortlist');
      }
    });
  });

  // Remove from shortlist
  $(document).on('click', '.remove-shortlist', function () {
    var userId = $(this).data('user-id');
    var button = $(this);

    if (!confirm('Are you sure you want to remove this profile from your shortlist?')) {
      return;
    }

    button.prop('disabled', true).text('Removing...');

    $.ajax({
      url: '/shortlist/remove',
      type: 'POST',
      data: {userId: userId},
      success: function (response) {
        if (response.success) {
          if (button.closest('.shortlist-entry').length) {
            // If on shortlist page, remove the entire entry
            button.closest('.shortlist-entry').fadeOut(300, function () {
              $(this).remove();
              checkEmptyShortlist();
            });
          } else {
            // If on profile page, change button back to "Add"
            button.removeClass('btn-danger remove-shortlist')
              .addClass('btn-success add-to-shortlist')
              .text('Add to Shortlist');
          }

          updateShortlistCount();
          showMessage(response.message, 'success');
        } else {
          showMessage(response.message, 'error');
          button.prop('disabled', false).text('Remove from Shortlist');
        }
      },
      error: function () {
        showMessage('An error occurred. Please try again.', 'error');
        button.prop('disabled', false).text('Remove from Shortlist');
      }
    });
  });

  // Save notes
  $(document).on('click', '.save-notes', function () {
    var userId = $(this).data('user-id');
    var button = $(this);
    var notesField = button.siblings('.notes-field');
    var notes = notesField.val();

    button.prop('disabled', true).text('Saving...');

    $.ajax({
      url: '/shortlist/updateNotes',
      type: 'POST',
      data: {
        userId: userId,
        notes: notes
      },
      success: function (response) {
        if (response.success) {
          showMessage(response.message, 'success');
          notesField.addClass('saved-flash');
          setTimeout(function () {
            notesField.removeClass('saved-flash');
          }, 1000);
        } else {
          showMessage(response.message, 'error');
        }
        button.prop('disabled', false).text('Save Notes');
      },
      error: function () {
        showMessage('An error occurred while saving notes.', 'error');
        button.prop('disabled', false).text('Save Notes');
      }
    });
  });

  // Check shortlist status for profile pages
  $('.shortlist-status-check').each(function () {
    var userId = $(this).data('user-id');
    var button = $(this);

    $.ajax({
      url: '/shortlist/checkStatus',
      type: 'GET',
      data: {userId: userId},
      success: function (response) {
        if (response.shortlisted) {
          button.removeClass('btn-success add-to-shortlist')
            .addClass('btn-danger remove-shortlist')
            .text('Remove from Shortlist');
        } else {
          button.removeClass('btn-danger remove-shortlist')
            .addClass('btn-success add-to-shortlist')
            .text('Add to Shortlist');
        }
        button.show();
      }
    });
  });

  // Update shortlist count in navigation
  function updateShortlistCount() {
    $.ajax({
      url: '/shortlist/count',
      type: 'GET',
      success: function (response) {
        $('.shortlist-count').text(response.count);
        if (response.count > 0) {
          $('.shortlist-count').show();
        } else {
          $('.shortlist-count').hide();
        }
      }
    });
  }

  // Check if shortlist is empty and show appropriate message
  function checkEmptyShortlist() {
    if ($('.shortlist-entry').length === 0) {
      $('.shortlist-grid').hide();
      $('.pagination').hide();
      $('.empty-shortlist').show();
    }
  }

  // Show success/error messages
  function showMessage(message, type) {
    var alertClass = type === 'success' ? 'alert-success' : 'alert-danger';
    var alert = $('<div class="alert ' + alertClass + ' alert-dismissible fade show" role="alert">' +
      message +
      '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>' +
      '</div>');

    // Insert alert at top of content
    $('.content').prepend(alert);

    // Auto dismiss after 5 seconds
    setTimeout(function () {
      alert.fadeOut();
    }, 5000);
  }

  // Initialize shortlist count on page load
  updateShortlistCount();
});

// Shortlist button component for profile cards
function createShortlistButton(userId, isShortlisted = false) {
  var buttonClass = isShortlisted ? 'btn-danger remove-shortlist' : 'btn-success add-to-shortlist';
  var buttonText = isShortlisted ? 'Remove from Shortlist' : 'Add to Shortlist';

  return '<button class="btn ' + buttonClass + '" data-user-id="' + userId + '">' + buttonText + '</button>';
}

// Add shortlist functionality to existing profile cards
function initializeShortlistButtons() {
  $('.profile-card').each(function () {
    var profileCard = $(this);
    var userId = profileCard.data('user-id');

    if (userId && !profileCard.find('.shortlist-btn').length) {
      var shortlistBtn = $(createShortlistButton(userId));
      shortlistBtn.addClass('shortlist-status-check').hide();
      profileCard.find('.profile-actions').append(shortlistBtn);
    }
  });
}
