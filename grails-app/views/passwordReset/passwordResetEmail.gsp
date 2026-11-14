<!-- grails-app/views/email/requestPwdReset.gsp -->
<html>
<body>
<p>Hello ${user.firstName},</p>

<p>You recently requested to reset your password. Click the link below to reset it:</p>

<p><a href="${resetLink}">Reset Password</a></p>

<p>This password reset link is only valid for the next 24 hours.</p>

<p>If you didn't request a password reset, please ignore this email.</p>

<p>Thanks,<br>Your Application Team</p>
</body>
</html>
