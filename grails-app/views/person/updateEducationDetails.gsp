<!doctype html>
<html>
<head>
  <meta name="layout" content="main"/>
</head>

<body>
<content tag="nav">
  <ul class="d-flex justify-content-end">
    <li><div class="badge badge-primary fixed-size-badge"><i class="fas fa-square"><span class="label-text">Admin
    </span></i></div></li>
    <li><a href="${createLink(controller: 'profile', action: 'index')}">Profile</a></li>
    <li><a href="${createLink(controller: 'search', action: 'index')}">Search</a></li>
    <li><a href="${createLink(controller: 'signIn', action: 'logOut')}">Sign Out</a></li>
  </ul>
</content>

<div id="content" role="main">
  <div class="container">
    <a href="${createLink(controller: 'person', action: 'index')}"
       class="same-line"><i class="fas fa-home embossed-link"></i></a>
    <a
      href="${createLink(controller: 'person', action: 'updateContactDetails', params: [id: params?.id, op: params?.op, con: params?.con])}"
      class="same-line"><i class="fas fa-chevron-left embossed-link"></i></a>

    <h1 class="same-line">Edit Education Details</h1>

    <g:if test="${flash.error}">
      <div id="alert" class="animate__animated animate__fadeIn animate__delay-0s alert alert-danger">
        ${flash.error}
      </div>
    </g:if>
    <g:if test="${flash.message}">
      <div id="alert" class="animate__animated animate__fadeIn animate__delay-0s alert alert-success">
        ${flash.message}
      </div>
    </g:if>
    <section class="row colset-2-its form-container">
      <div class="col d-flex justify-content-start">
        <g:if test="${educations}">
          <ul class="nav nav-tabs" id="educationDetailTabs" role="tablist">
            <g:each in="${educations}" var="contactDetail" status="i">
              <li class="nav-item" role="presentation">
                <button class="nav-link ${i == 0 ? 'active' : ''}" id="educationDetail${i}-tab" data-toggle="tab"
                        data-target="#educationDetail${i}" type="button" role="tab" aria-controls="educationDetail${i}"
                        aria-selected="${i == 0 ? 'true' : 'false'}">
                  Education Detail ${i + 1}
                </button>
              </li>
            </g:each>
          </ul>
        </g:if>
        <g:else>
          <div class="d-flex align-items-center h-auto">
            <strong class="same-line text-center">No Education Details found. Click the</strong>
            <img src="${resource(dir: 'images', file: 'add_new.png')}" class="same-line text-nowrap custom-4x4em"
                 alt="Add Button Image">
            <strong class="same-line text-center">button to add a new Education detail !!</strong>
          </div>
        </g:else>
      </div>

      <div class="col d-flex justify-content-end text-nowrap">
        <a
          href="${createLink(controller: 'person', action: 'addEducationDetail', params: [id: params?.id, op: params?.op, con: params?.con])}"
          class="btn btn-primary toolbar-button" role="button"><i class="fas fa-add"></i></a>
        <a
          href="${createLink(controller: 'person', action: 'updateProfessionDetails', params: [id: params?.id, op: params?.op, con: params?.con])}"
          class="btn btn-primary toolbar-button" role="button"><i class="fas fa-chevron-right"></i></a>
      </div>

      <div class="tab-content" id="educationDetailTabsContent">
        <g:each in="${educations}" var="educationDetail" status="i">
          <div class="tab-pane fade ${i == 0 ? 'show active' : ''}" id="educationDetail${i}" role="tabpanel"
               aria-labelledby="educationDetail${i}-tab">
            <form
              action="${createLink(controller: 'person', action: 'saveEducationDetails')}"
              method="post">
              <input type="hidden" name="id" value="${educationDetail?.id}"/>
              <input type="hidden" name="op" value="${params?.op}"/>
              <input type="hidden" name="con" value="${params?.con}"/>

              <section class="row colset-2-its justify-content-center">
                <div class="mb-3"><div class="input-container">
                  <input type="text" class="form-control" id="name" name="name" placeholder=""
                         value="${educationDetail?.name}" required>
                  <label for="name" class="form-label">Education</label>
                </div></div>
              </section>
              <section class="row colset-2-its justify-content-center">
                <div class="mb-3"><div class="input-container">
                  <input type="text" class="form-control" id="institution" name="institution" placeholder=""
                         value="${educationDetail?.institution}"
                         required>
                  <label for="institution" class="form-label">Institution</label>
                </div></div>
              </section>
              <section class="row colset-2-its justify-content-center">
                <div class="mb-3"><div class="input-container">
                  <input type="text" class="form-control" id="institutionLocation" name="institutionLocation"
                         placeholder="" value="${educationDetail?.institutionLocation}"
                         required>
                  <label for="institutionLocation" class="form-label">Institution Location</label>
                </div></div>
              </section>
              <section class="row colset-2-its justify-content-center">
                <div class="mb-3"><div class="input-container">
                  <input type="text" class="form-control" id="university" name="university" placeholder=""
                         value="${educationDetail?.university}"
                         required>
                  <label for="university" class="form-label">University</label>
                </div></div>
              </section>

              <section class="row colset-2-its justify-content-center">
                <div class="mb-3"><div class="input-container">
                  <input type="text" class="form-control" id="universityLocation" name="universityLocation"
                         placeholder="" value="${educationDetail?.universityLocation}"
                         required>
                  <label for="universityLocation" class="form-label">University Location</label>
                </div></div>
              </section>
              <section class="row colset-2-its justify-content-center">
                <div class="checkbox-wrapper-42">
                  <g:if test="${educationDetail?.isHighest == true}">
                    <input name="isHighest" id="isHighest${i}" type="checkbox" checked/>
                  </g:if>
                  <g:else>
                    <input name="isHighest" id="isHighest${i}" type="checkbox"/>
                  </g:else>
                  <label class="cbx" for="isHighest${i}"></label>
                  <label class="lbl" for="isHighest${i}">Is Highest Education</label>
                </div>
              </section>

              <div class="d-flex justify-content-end">
                <button class="btn btn-primary" type="submit"><i class="fas fa-save"
                                                                 aria-hidden="true">&nbsp;&nbsp;Save</i>
                </button>
              </div>
            </form>
          </div>
        </g:each>
      </div>
    </section>
  </div>
</div>

<script type="text/javascript">
  document.addEventListener("DOMContentLoaded", function () {

  });
</script>
</body>
</html>
