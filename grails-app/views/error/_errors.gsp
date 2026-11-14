<div class="alert alert-danger">
  <h4>Error ${request.'javax.servlet.error.status_code'}</h4>

  <p>${request.'javax.servlet.error.message'}</p>
  <g:if env="development">
    <pre><g:eachError>${it}</g:eachError></pre>
    <pre>${request.'javax.servlet.error.exception'?.stackTrace?.join('\n')}</pre>
  </g:if>
</div>
