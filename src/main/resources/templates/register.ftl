<#include "/common/base.ftl">
<@content>
    <div class="centre-content generic-container">
        <form hx-post="/api/register" hx-target="#response">
            <label>Username: <input type="text" name="username"></label><br>
            <label>Email Address: <input type="email" name="email-address"></label><br>
            <input type="submit" value="Submit">
        </form>
        <div id="response"></div>
    </div>
</@content>