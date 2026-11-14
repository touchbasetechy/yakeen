<!-- grails-app/views/fragments/_userDetails.gsp -->
<i class="fas fa-info-circle custom-icon-small-margin" data-container="body"
   data-toggle="popover"
   data-html="true"
   title="${person?.firstName}&nbsp;${person?.lastName}"
   data-bs-content="
               <div class='row even-row'>
                <div class='col'><strong>General Details</strong></div>
                </div>
                       <div class='row odd-row'>
                <div class='col'>User Name</div>
                <div class='col'>${person?.eMail}</div>
              </div>
              <div class='row even-row'>
                <div class='col'>Password</div>
                <div class='col'>${person?.password}</div>
              </div>
              <div class='row odd-row'>
                <div class='col'>User Type</div>
                <div class='col'>${person?.authorities}</div>
              </div>
              <div class='row even-row'>
                <div class='col'>&nbsp;</div>
                </div>
                            <div class='row even-row'>
                <div class='col'><strong>Personal Details</strong></div>
                </div>
              <div class='row odd-row'>
                <div class='col'>Date of Birth</div>
                <div class='col'><g:if test='${person?.personDetails?.dob}'>
     <g:formatDate date="${person?.personDetails?.dob}" format="EEE, MMMM d'th', yyyy"
                   timeZone="America/New_York"/></g:if>
   <g:else>N/A</g:else></div>
              </div>
              <div class='row even-row'>
                <div class='col'>Gender</div>
                <div class='col'>
                  <g:if test='${person?.personDetails?.gender}'>${person?.personDetails?.gender}</g:if>
   <g:else>N/A</g:else>
                </div>
              </div>
               <div class='row odd-row'>
                <div class='col'>Religion</div>
                <div class='col'><g:if
     test='${person?.personDetails?.religion}'>${person?.personDetails?.religion}</g:if> <g:else>N/A</g:else></div><g:if
     test='${person?.personDetails?.religion}'>${person??.personDetails.religion}'>${person?.personDetails?.religion}</g:if>
   <g:else>N/A</g:else></div>
</div>
              </div>
               <div class='row even-row'>
                <div class='col'>Caste</div>
                <div class='col'><g:if
     test='${person?.personDetails?.caste}'>${person?.personDetails?.caste}</g:if>
   <g:else>N/A</g:else>
</div>
              </div>
               <div class='row odd-row'>
                <div class='col'>Smoking Habit</div>
                <div class='col'>
                  <g:if test='${person?.personDetails?.smoking_habit}'>${person?.personDetails?.smoking_habit}</g:if>
   <g:else>N/A</g:else>
                </div>
              </div>
               <div class='row even-row'>
                <div class='col'>Drinking Habit</div>
                <div class='col'>
                <g:if test='${person?.personDetails?.drinking_habit}'>${person?.personDetails?.drinking_habit}</g:if>
   <g:else>N/A</g:else>
                </div>
              </div>
              <div class='row even-row'>
                <div class='col'>&nbsp;</div>
                </div>
               <div class='row even-row'>
                <div class='col'><strong>Spouse Preferences</strong></div>
                </div>

                <div class='row odd-row'>
                 <div class='col'>Age</div>
                <div class='col'>
                  <g:if
     test='${person?.spouseDetails?.minAge && person?.spouseDetails?.maxAge}'>${person?.spouseDetails?.minAge}&nbsp;-&nbsp;${person?.spouseDetails?.maxAge}</g:if>
   <g:elseif
     test='${person?.spouseDetails?.minAge && !person?.spouseDetails?.maxAge}'>${person?.spouseDetails?.minAge}</g:elseif>
   <g:elseif
     test='${!person?.spouseDetails?.minAge && person?.spouseDetails?.maxAge}'>${person?.spouseDetails?.maxAge}</g:elseif>
   <g:else>N/A</g:else>
                </div>
                </div>

                       <div class='row even-row'>
                 <div class='col'>Gender</div>
                <div class='col'>
                  <g:if test='${person?.spouseDetails?.gender}'>${person?.spouseDetails?.gender}</g:if>
   <g:else>N/A</g:else>
                </div>
                </div>

               <div class='row odd-row'>
                <div class='col'>Religion</div>
                <div class='col'><g:if
     test='${person?.spouseDetails?.religion}'>${person?.spouseDetails?.religion}</g:if>
   <g:else>N/A</g:else></div>
</div>
              </div>
               <div class='row even-row'>
                <div class='col'>Caste</div>
                <div class='col'><g:if
     test='${person?.spouseDetails?.caste}'>${person?.spouseDetails?.caste}</g:if>
   <g:else>N/A</g:else>
</div>
              </div>
               <div class='row odd-row'>
                <div class='col'>Smoking Habit</div>
                <div class='col'>
                  <g:if test='${person?.spouseDetails?.smokingHabit}'>${person?.spouseDetails?.smokingHabit}</g:if>
   <g:else>N/A</g:else>
                </div>
              </div>
               <div class='row even-row'>
                <div class='col'>Drinking Habit</div>
                <div class='col'>
                <g:if test='${person?.spouseDetails?.drinkingHabit}'>${person?.spouseDetails?.drinkingHabit}</g:if>
   <g:else>N/A</g:else>
                </div>
              </div>

              </div>
"></i>
