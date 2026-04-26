<#macro content>
<!DOCTYPE html>
<head>
    <meta charset="UTF-8">
    <script src="/static/htmx.min.js"></script>
    <link rel="stylesheet" href="/static/styles2.css">
    <link rel="apple-touch-icon" sizes="180x180" href="/apple-touch-icon.png">
    <link rel="icon" type="image/png" sizes="32x32" href="/favicon-32x32.png">
    <link rel="icon" type="image/png" sizes="16x16" href="/favicon-16x16.png">
    <link rel="manifest" href="/site.webmanifest">
    <title>AceOfGrades</title>
</head>
<body>
<div id="navbar">
    <ul>
        <li class="mobile-hide" style="left: 0; top: 0; bottom: 0; position: absolute; padding: 0; width: auto" onclick="window.location.href='/';">
            <img src="/static/aceofgrades.png" alt="🃏 aceofgrad.es 🃏" style="position: relative; height: 100%; width: auto; padding: 5px 10px 7px 10px">
        </li>
        <li class="desktop-hide" style="left: 0; top: 0; bottom: 0; position: absolute; padding: 0; width: auto" onclick="window.location.href='/';">
            <img src="/static/aceofgrades_mobile.png" alt="🃏 aceofgrad.es 🃏" style="position: relative; height: 100%; width: auto; padding: 5px 10px 7px 10px">
        </li>
        <li class="mobile-hide" >
            <a href="/">Home</a>
        </li>
        <li>
            <a href="/leaderboard">Leaderboard</a>
        </li>
        <#if user.role == "ADMIN">
            <li>
                <a href="">Write Post</a>
            </li>
        </#if>
        <#if user.role == "ANYONE">
            <li>
                <a href="/explore">Explore</a>
            </li>
            <li>
                <a href="/register">Register</a>
            </li>
            <li>
                <a href="/login">Log In</a>
            </li>
        <#elseif user.role == "USER" || user.role == "ADMIN">
            <li>
                <a href="/packs/">Packs</a>
            </li>
            <li>
                <a href="/explore">Explore</a>
            </li>
            <li>
                <a href="/profiles/${user.getID()}">${user.username}</a>
            </li>
            <li style="right: 0; position: absolute">
                <form action="/forms/logout" method="POST">
                    <button>Log Out</button>
                </form>
            </li>
        </#if>
    </ul>
</div>
<div id="content">
    <#nested>
</div>
<footer style="color: #FFFFFF; background-color: #555555">
    <p>Sponsored by:</p>
    <p>the legendmixer foundation</p>
</footer>
</body>
</#macro>