<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
  <title>No Internet Connection</title>
  <style>
  .no-internet-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    min-height: 60vh;
    text-align: center;
    padding: 20px;
  }

  .no-internet-icon {
    font-size: 80px;
    margin-bottom: 20px;
  }

  .no-internet-title {
    font-size: 32px;
    font-weight: bold;
    color: #2c3e50;
    margin-bottom: 15px;
  }

  .no-internet-message {
    font-size: 18px;
    color: #7f8c8d;
    margin-bottom: 30px;
    max-width: 500px;
  }

  .retry-button {
    padding: 12px 30px;
    font-size: 16px;
    background-color: #3498db;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.3s;
  }

  .retry-button:hover {
    background-color: #2980b9;
  }

  .retry-button:disabled {
    background-color: #95a5a6;
    cursor: not-allowed;
  }

  .status-message {
    margin-top: 15px;
    font-size: 14px;
    color: #7f8c8d;
  }
  </style>
</head>

<body>
<div class="no-internet-container">
  <div class="no-internet-icon">🌐❌</div>

  <h1 class="no-internet-title">No Internet Connection</h1>

  <p class="no-internet-message">
    We're unable to connect to the internet. Please check your connection and try again.
  </p>
  <button class="retry-button" id="retryBtn" onclick="retryConnection()">
    Retry Connection
  </button>

  <p class="status-message" id="statusMessage"></p>
</div>

<script>
  let retryCount = 0;
  const maxRetries = 5;

  function retryConnection() {
    const btn = document.getElementById('retryBtn');
    const statusMsg = document.getElementById('statusMessage');

    btn.disabled = true;
    btn.textContent = 'Checking...';
    statusMsg.textContent = 'Checking internet connection...';

    // Check if browser is online
    if (navigator.onLine) {
      // Reload page to trigger server-side check
      location.reload();
    } else {
      retryCount++;
      statusMsg.textContent = `Attempt ${retryCount}/${maxRetries} - Still offline`;

      setTimeout(() => {
        btn.disabled = false;
        btn.textContent = 'Retry Connection';

        if (retryCount < maxRetries) {
          statusMsg.textContent = 'Please try again when your connection is restored';
        }
      }, 2000);
    }
  }

  // Auto-check every 30 seconds
  setInterval(() => {
    if (navigator.onLine) {
      location.reload();
    }
  }, 30000);

  // Listen for online event
  window.addEventListener('online', () => {
    location.reload();
  });
</script>
</body>
</html>
