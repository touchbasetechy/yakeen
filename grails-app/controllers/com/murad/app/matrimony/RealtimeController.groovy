import com.app.matrimony.StatusUpdateCommand
import com.app.matrimony.TypingCommand
import com.murad.app.matrimony.NotificationService
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.annotation.SubscribeMapping

@Secured(['ROLE_USER'])
class RealtimeController {

  def springSecurityService
  NotificationService notificationService

  @MessageMapping("/user.status")
  @SendTo("/topic/user-status")
  def updateUserStatus(StatusUpdateCommand statusCommand) {
    //, SimpMessageHeaderAccessor headerAccessor) {
    def user = springSecurityService.currentUser
    return [
      userId   : user.id,
      username : user.username,
      status   : statusCommand.status,
      timestamp: System.currentTimeMillis()
    ]
  }

  @MessageMapping("/typing")
  def handleTyping(TypingCommand typingCommand) {
    def user = springSecurityService.currentUser
    notificationService.sendTypingIndicator(
      user.id,
      typingCommand.receiverId,
      typingCommand.isTyping
    )
  }

  @SubscribeMapping("/user/{userId}/queue/interests")
  def subscribeToInterests(Long userId) {
    def currentUser = springSecurityService.currentUser
    if (currentUser.id != userId) {
      throw new IllegalAccessException("Cannot subscribe to another user's interests")
    }

    // Return current unread count
    return [count: notificationService.getUnreadInterestCount(userId)]
  }
}

