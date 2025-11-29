<#macro content>
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="/static/styles.css">
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
                <a href="">Sign Up</a>
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