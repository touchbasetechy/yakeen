class RealtimeInterests {
  constructor() {
    this.socket = null;
    this.stompClient = null;
    this.userId = null;
    this.isConnected = false;
    this.reconnectAttempts = 0;
    this.maxReconnectAttempts = 5;
    this.reconnectInterval = 5000; // 5 seconds

    this.init();
  }

  init() {
    // Get current user ID from page
    this.userId = document.querySelector('[data-user-id]')?.getAttribute('data-user-id');
    if (!this.userId) {
      console.warn('User ID not found, real-time features disabled');
      return;
    }

    this.connect();
    this.setupEventListeners();
  }

  connect() {
    console.log('Connecting to WebSocket...');

    this.socket = new SockJS('/ws');
    this.stompClient = Stomp.over(this.socket);

    // Disable debug logging in production
    this.stompClient.debug = (str) => {
      if (window.location.hostname === 'localhost') {
        console.log(str);
      }
    };

    this.stompClient.connect({},
      (frame) => this.onConnected(frame),
      (error) => this.onError(error)
    );
  }

  onConnected(frame) {
    console.log('Connected to WebSocket:', frame);
    this.isConnected = true;
    this.reconnectAttempts = 0;

    // Subscribe to interest notifications
    this.stompClient.subscribe(`/user/${this.userId}/queue/interests`,
      (notification) => this.handleInterestNotification(JSON.parse(notification.body))
    );

    // Subscribe to interest responses
    this.stompClient.subscribe(`/user/${this.userId}/queue/interest-responses`,
      (response) => this.handleInterestResponse(JSON.parse(response.body))
    );

    // Subscribe to interest count updates
    this.stompClient.subscribe(`/user/${this.userId}/queue/interest-count`,
      (countUpdate) => this.updateInterestCount(JSON.parse(countUpdate.body))
    );

    // Subscribe to typing indicators
    this.stompClient.subscribe(`/user/${this.userId}/queue/typing`,
      (typing) => this.handleTypingIndicator(JSON.parse(typing.body))
    );

    // Subscribe to user status updates
    this.stompClient.subscribe('/topic/user-status',
      (status) => this.handleUserStatusUpdate(JSON.parse(status.body))
    );

    // Send online status
    this.sendUserStatus('online');

    // Update UI to show connected status
    this.updateConnectionStatus(true);
  }

  onError(error) {
    console.error('WebSocket error:', error);
    this.isConnected = false;
    this.updateConnectionStatus(false);

    // Attempt to reconnect
    this.attemptReconnect();
  }

  attemptReconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      console.log(`Attempting to reconnect (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);

      setTimeout(() => {
        this.connect();
      }, this.reconnectInterval);
    } else {
      console.error('Max reconnection attempts reached');
      this.showReconnectMessage();
    }
  }

  handleInterestNotification(notification) {
    console.log('Interest notification received:', notification);

    // Show toast notification
    this.showToastNotification({
      type: 'interest',
      title: 'New Interest Received!',
      message: `${notification.senderName} ${notification.message}`,
      avatar: notification.senderAvatar,
      timestamp: notification.timestamp,
      actionUrl: `/interest/received`
    });

    // Update interest count badge
    this.updateInterestBadge();

    // Play notification sound
    this.playNotificationSound();

    // Add to recent notifications list
    this.addToNotificationsList(notification);

    // Show browser notification if permission granted
    this.showBrowserNotification(
      'New Interest Received',
      `${notification.senderName} sent you an interest`,
      notification.senderAvatar
    );
  }

  handleInterestResponse(response) {
    console.log('Interest response received:', response);

    const statusText = response.status === 'accepted' ? 'accepted' : 'declined';
    const statusColor = response.status === 'accepted' ? 'success' : 'danger';

    this.showToastNotification({
      type: 'response',
      title: `Interest ${statusText.charAt(0).toUpperCase() + statusText.slice(1)}`,
      message: `${response.receiverName} ${statusText} your interest`,
      avatar: response.receiverAvatar,
      timestamp: response.timestamp,
      color: statusColor,
      actionUrl: `/interest/sent`
    });

    // Play different sound for acceptance vs rejection
    this.playNotificationSound(response.status === 'accepted' ? 'success' : 'error');
  }

  updateInterestCount(countData) {
    const badge = document.querySelector('.interest-count-badge');
    if (badge) {
      badge.textContent = countData.count;
      badge.style.display = countData.count > 0 ? 'inline' : 'none';
    }
  }

  handleTypingIndicator(typing) {
    const chatContainer = document.querySelector(`[data-chat-user="${typing.senderId}"]`);
    if (chatContainer) {
      const typingIndicator = chatContainer.querySelector('.typing-indicator');
      if (typing.isTyping) {
        typingIndicator.style.display = 'block';
        typingIndicator.textContent = 'typing...';
      } else {
        typingIndicator.style.display = 'none';
      }
    }
  }

  handleUserStatusUpdate(status) {
    // Update user online status indicators
    const userElements = document.querySelectorAll(`[data-user-id="${status.userId}"]`);
    userElements.forEach(element => {
      const statusIndicator = element.querySelector('.online-status');
      if (statusIndicator) {
        statusIndicator.className = `online-status ${status.isOnline ? 'online' : 'offline'}`;
        statusIndicator.title = status.isOnline ? 'Online' : 'Offline';
      }
    });
  }

  showToastNotification(options) {
    // Create toast notification element
    const toast = document.createElement('div');
    toast.className = `toast-notification ${options.type} ${options.color || 'primary'}`;
    toast.innerHTML = `
            <div class="toast-header">
                <img src="${options.avatar}" class="toast-avatar" alt="Avatar">
                <strong class="toast-title">${options.title}</strong>
                <button type="button" class="toast-close" onclick="this.parentElement.parentElement.remove()">×</button>
            </div>
            <div class="toast-body">
                ${options.message}
                ${options.actionUrl ? `<a href="${options.actionUrl}" class="toast-action">View</a>` : ''}
            </div>
        `;

    // Add to notification container
    const container = document.querySelector('.toast-container') || this.createToastContainer();
    container.appendChild(toast);

    // Auto-remove after 5 seconds
    setTimeout(() => {
      if (toast.parentElement) {
        toast.remove();
      }
    }, 5000);

    // Animate in
    setTimeout(() => toast.classList.add('show'), 100);
  }

  createToastContainer() {
    const container = document.createElement('div');
    container.className = 'toast-container';
    document.body.appendChild(container);
    return container;
  }

  playNotificationSound(type = 'default') {
    try {
      const audio = new Audio(`/assets/sounds/notification-${type}.mp3`);
      audio.volume = 0.3;
      audio.play().catch(e => console.log('Could not play notification sound:', e));
    } catch (e) {
      console.log('Notification sound not available');
    }
  }

  showBrowserNotification(title, body, icon) {
    if ('Notification' in window && Notification.permission === 'granted') {
      const notification = new Notification(title, {
        body: body,
        icon: icon,
        tag: 'matrimony-interest'
      });

      notification.onclick = () => {
        window.focus();
        notification.close();
      };

      setTimeout(() => notification.close(), 5000);
    }
  }

  updateInterestBadge() {
    // Fetch updated count and update badge
    fetch('/api/interests/count', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      }
    })
      .then(response => response.json())
      .then(data => {
        this.updateInterestCount(data);
      })
      .catch(error => console.error('Error fetching interest count:', error));
  }

  setupEventListeners() {
    // Handle page visibility change
    document.addEventListener('visibilitychange', () => {
      if (document.hidden) {
        this.sendUserStatus('away');
      } else {
        this.sendUserStatus('online');
      }
    });

    // Handle before page unload
    window.addEventListener('beforeunload', () => {
      this.sendUserStatus('offline');
      this.disconnect();
    });

    // Request notification permission
    if ('Notification' in window && Notification.permission === 'default') {
      Notification.requestPermission();
    }
  }

  sendUserStatus(status) {
    if (this.isConnected && this.stompClient) {
      this.stompClient.send('/app/user.status', {}, JSON.stringify({
        status: status,
        timestamp: Date.now()
      }));
    }
  }

  sendTypingIndicator(receiverId, isTyping) {
    if (this.isConnected && this.stompClient) {
      this.stompClient.send('/app/typing', {}, JSON.stringify({
        receiverId: receiverId,
        isTyping: isTyping
      }));
    }
  }

  updateConnectionStatus(connected) {
    const statusElement = document.querySelector('.connection-status');
    if (statusElement) {
      statusElement.className = `connection-status ${connected ? 'connected' : 'disconnected'}`;
      statusElement.textContent = connected ? 'Connected' : 'Disconnected';
    }
  }

  showReconnectMessage() {
    const message = document.createElement('div');
    message.className = 'reconnect-message';
    message.innerHTML = `
            <div class="alert alert-warning">
                <strong>Connection Lost</strong>
                <p>Real-time notifications are currently unavailable.</p>
                <button onclick="window.realtimeInterests.connect()" class="btn btn-sm btn-primary">
                    Reconnect
                </button>
            </div>
        `;
    document.body.appendChild(message);
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.disconnect();
    }
    this.isConnected = false;
  }
}
