<div class="row justify-content-center">
  <a href="${createLink(controller: 'DBoard', action: 'index')}"
     class="same-line"><i class="fas fa-chevron-left embossed-link"></i></a>

  <h1 class="same-line">${header}</h1>

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
</div>
