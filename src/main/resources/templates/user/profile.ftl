<#include "/common/base.ftl">
<@content>
    <div class="generic-container left-content">
        <div class="centre-wide">
            <#if is_owner>
                <h2>Hello ${user_data.getUsername()}</h2>
                <p>Here you can see various stats about your profile.</p>
            <#else>
                <h2>Profile for ${user_data.getUsername()}</h2>
                <p>Here you can see various stats about this profile.</p>
            </#if>
        </div>
        <div class="left-content">
            <h3><u>User stats</u></h3>
            <p>
                <#if is_owner>
                    You have
                <#else>
                    ${user_data.getUsername()} has
                </#if>
                 created
                <b>${user_stats.getNumPacks()} pack<#if user_stats.getNumPacks() != 1>s</#if></b>,
                that in total contain <b>${user_stats.getNumCards()} card<#if user_stats.getNumCards() != 1>s</#if></b>.
                In total,
                <#if is_owner>
                    you have
                <#else>
                    ${user_data.getUsername()} has
                </#if>
                 attempted <b>${user_stats.getNumAttempts()} card<#if user_stats.getNumAttempts() != 1>s</#if></b>,
                with <b>${user_stats.getNumCorrect()}</b> of those attempts being correct.
            </p>
            <#if is_owner && has_cards>
                <div style="width: 70%">
                    <h3><u>Your best card</u></h3>
                    <div class="card prevent-select" style="background: ${best_card.getFrontColor()}" onclick="window.location.href='/packs/${best_card_pack}';">
                        <p>${best_card.getFront()?no_esc}</p>
                    </div>
                    <h3><u>Your worst card</u></h3>
                    <div class="card prevent-select" style="background: ${worst_card.getFrontColor()}" onclick="window.location.href='/packs/${worst_card_pack}';">
                        <p>${worst_card.getFront()?no_esc}</p>
                    </div>
                </div>
            </#if>
        </div>
    </div>
</@content>