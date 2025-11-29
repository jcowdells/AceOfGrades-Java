<#macro content>
<!DOCTYPE html>
<head>
    <meta charset="UTF-8">
    <script src="/static/htmx.min.js"></script>
    <link rel="stylesheet" href="/static/styles.css">
    <link rel="apple-touch-icon" sizes="180x180" href="/apple-touch-icon.png">
    <link rel="icon" type="image/png" sizes="32x32" href="/favicon-32x32.png">
    <link rel="icon" type="image/png" sizes="16x16" href="/favicon-16x16.png">
    <link rel="manifest" href="/site.webmanifest">
    <title>aceofgrad.es</title>
</head>
<body>
<div id="navbar">
    <ul>
        <li style="left: 0; position: absolute">
            <a href="">🃏 aceofgrad.es 🃏</a>
        </li>
        <li>
            <a href="">Home</a>
        </li>
        <#if user.role == "ADMIN">
            <li>
                <a href="">Write Post</a>
            </li>
        </#if>
        <#if user.role == "ANYONE">
            <li>
                <a href="/register">Register</a>
            </li>
            <li>
                <a href="">Log In</a>
            </li>
        <#elseif user.role == "USER" || user.role == "ADMIN">
            <li>
                <a href="">Packs</a>
            </li>
            <li>
                <a href="">View Cards</a>
            </li>
            <li>
                <a href="">Editor</a>
            </li>
            <li>
                <a href="">${user.username}</a>
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
</html>
</#macro>