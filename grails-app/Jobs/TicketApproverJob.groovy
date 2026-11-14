import com.murad.app.matrimony.TicketService
import grails.artefact.Artefact
import org.quartz.DisallowConcurrentExecution
import org.quartz.JobExecutionContext

@DisallowConcurrentExecution
@Artefact("Job")
@SuppressWarnings("GroovyUnusedDeclaration")
class TicketApproverJob {
  TicketService ticketService

  static triggers = {
    cron name: 'ticketApprovalCronTrigger',
      cronExpression: '0 0/5 * * * ?' // Every 5 minutes
  }

  def execute(JobExecutionContext context) {
    log.info("TicketsApproverJob starting...")
    try {
      int count = ticketService.batchAutoApproveAndAssign()
      log.info("Auto-approved and assigned ${count} tickets")
    } catch (Exception e) {
      log.error("Error in TicketsApproverJob", e)
      // Consider adding error notification here
    }
  }
}
