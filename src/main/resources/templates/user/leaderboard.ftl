<#include "/common/base.ftl">
<@content>
    <div class="spacious-container">
        <h2><u>Leaderboard</u></h2>
        <ol>
            <#list leaderboard as lb>
                <li>
                    ${lb.getUsername()} - ${lb.getNumCards()} cards
                </li>
            </#list>
        </ol>
    </div>
</@content>