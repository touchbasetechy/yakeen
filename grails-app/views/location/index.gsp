<html>
<head>
    <meta name="layout" content="main"/>
    <title>Location Demo</title>
</head>

<body>
<g:set var="currentSlot" value="${request.getAttribute('currentSlot')}"/>

<div id="status">Detecting location...</div>

<asset:javascript src="application"/>
<script>
    function setStatus(msg) {
        console.log('[location]', msg);
        document.getElementById('status').innerText = msg;
    }

    function saveBrowserLocation(lat, lng) {
        setStatus('Got coordinates, saving...');
        $.ajax({
            url: '${createLink(controller: "location", action: "saveBrowserLocation", params: [slot: currentSlot])}',
            data: {lat: lat, lng: lng},
            dataType: 'json',          // <-- force JSON parsing, skip auto-detect
            success: function (data) {
                console.log("data ", data);
                if (data && data.success === false) {
                    setStatus('Validation failed: ' + data.errors.join('; '));
                } else {
                    setStatus('Location saved (browser): ' + data.location.latitude + ', ' + data.location.longitude);
                }
            },
            error: function (xhr, status, err) {
                setStatus('Error saving browser location: ' + xhr.status + ' ' + err);
            }
        });
    }

    function saveIpLocation() {
        setStatus('Falling back to IP-based location...');
        $.ajax({
            url: '${createLink(controller: "location", action: "saveIpLocation", params: [slot: currentSlot])}',
            dataType: 'json',          // <-- force JSON parsing, skip auto-detect

            success: function (data) {
                setStatus('Location saved (IP fallback): ' + data.location.city + ', ' + data.location.country);
            },
            error: function (xhr, status, err) {
                setStatus('Error saving IP location: ' + xhr.status + ' ' + err);
            }
        });
    }

    if (navigator.geolocation) {
        setStatus('Requesting browser location...');
        navigator.geolocation.getCurrentPosition(
            function (position) {
                console.log(position.coords.latitude, position.coords.longitude);

                saveBrowserLocation(position.coords.latitude, position.coords.longitude);
            },
            function (error) {
                // Permission denied, timeout, or position unavailable -> fallback to IP
                setStatus('Browser geolocation failed (code ' + error.code + '): ' + error.message);
                saveIpLocation();
            },
            {timeout: 15000, enableHighAccuracy: false}
        );
    } else {
        setStatus('Browser does not support geolocation.');
        saveIpLocation();
    }
</script>
</body>
</html>
