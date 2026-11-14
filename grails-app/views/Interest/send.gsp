<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>Send Interest</title>
  <asset:stylesheet src="interests.css"/>
</head>

<body>
<div class="container">
  <div class="row justify-content-center">
    <div class="col-md-6">
      <div class="card">
        <div class="card-header">
          <h4>Send Interest</h4>
        </div>

        <div class="card-body">
          <div class="text-center mb-4">
            <img src="${createLink(controller: 'user', action: 'avatar', id: receiver.id)}"
                 class="profile-avatar-large" alt="Profile Picture">
            <h5 class="mt-2">${receiver}</h5>
          </div>

          <g:form action="send" method="post">
            <g:hiddenField name="receiverId" value="${receiver.id}"/>

            <div class="form-group">
              <label for="message">Personal Message (Optional)</label>
              <g:textArea name="message" class="form-control" rows="4"
                          placeholder="Write a brief message to introduce yourself..."/>
              <small class="form-text text-muted">
                Keep it respectful and genuine. Maximum 500 characters.
              </small>
            </div>

            <div class="text-center">
              <button type="submit" class="btn btn-primary">
                <i class="fa fa-heart"></i> Send Interest
              </button>
              <g:link controller="user" action="show" id="${receiver.id}"
                      class="btn btn-secondary ml-2">
                Cancel
              </g:link>
            </div>
          </g:form>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>
