<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<div id="navbar">
    <ul>
        <li style="left: 0; position: absolute">
            <a href="{{ url_for("index") }}">🃏 aceofgrad.es 🃏</a>
        </li>
        <li>
            <a href="{{ url_for("index") }}">Home</a>
        </li>
        <li>
            <a href="{{ url_for("packs") }}">Packs</a>
        </li>
        <li>
            <a href="{{ url_for("packs_view") }}">View Cards</a>
        </li>
        <li>
            <a href="{{ url_for("editor_select") }}">Editor</a>
        </li>
    </ul>
</div>
<div id="content">
    <@block name="content">

    </@block>
</div>
<footer style="color: #FFFFFF; background-color: #555555">
    <p>Sponsored by:</p>
    <p>the legendmixer foundation</p>
</footer>
</body>
</html>