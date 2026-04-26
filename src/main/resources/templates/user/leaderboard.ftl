<#include "/common/base.ftl">
<@content>
    <div class="spacious-container" style="margin: 20px">
        <h2><u>Leaderboard</u></h2>
        <p>The leaderboard measures the number of cards flipped.</p>
        <ol style="list-style-position: inside">
            <#list leaderboard as lb>
                <li>
                    ${lb.getUsername()} - ${lb.getNumCards()} cards
                </li>
            </#list>
        </ol>
    </div>
</@content>