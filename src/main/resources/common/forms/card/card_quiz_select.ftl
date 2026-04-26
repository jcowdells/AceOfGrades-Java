<#include "/common/forms/input/form_submit.ftl">
<form hx-post="/forms/packs/${pack_id}/quiz/start" hx-target="this" hx-swap="outerHTML">
    <div id="random-description" hidden>
        <h4>Random mode</h4>
        <p>Choose a number of cards randomly out of the pack.</p>
    </div>
    <div id="weakest-description" hidden>
        <h4>Weakest mode</h4>
        <p>Choose the weakest 'n' cards out of the pack, based on the ratio of attempts to correct answers for each card.</p>
    </div>
    <div id="burnout-description" hidden>
        <h4>Burnout mode</h4>
        <p>Like weakest mode, chooses the weakest 'n' cards out of the pack. If a card is marked incorrect, it is moved back to the bottom of the pile so that it can be reattempted.</p>
    </div>
    <div class="form">
        <label for="quiz-style">Choose a quiz style:</label>
        <select id="quiz-style" name="quiz-style">
            <option value="random">Random</option>
            <option value="weakest">Weakest</option>
            <option value="burnout">Burnout</option>
        </select>
    </div>
    <br>
    <div id="selected-description" style="margin-left: 2rem"></div>
    <br>
    <div class="form">
        <label for="num-cards">Choose the pile size:</label>
        <input type="number" id="num-cards" name="num-cards" min="0" max="${num_cards}" value="${num_cards}">
    </div>
    <#if is_creator || spotlights?has_content>
        <div>
            <h4>Spotlights</h4>
            <p>Spotlights are a curated subset of a pack that focuses on a particular part of a topic; they tend to be small, bite-sized, 5 minute activities to refresh your memory on that topic.</p>
        </div>
        <div id="spotlights">
            <#list spotlights as spotlight>
                <div class="spotlight">
                    <span>
                        <button class="delete-button" data-spotlight-id="${spotlight.getID()}">X</button>
                    </span>
                    <button type="submit" name="spotlight" value="${spotlight.getID()}" style="padding-right: 1.5rem">${spotlight.getName()}</button>
                </div>
            </#list>
            <#if is_creator>
                <div class="spotlight">
                    <button type="submit" name="new-spotlight" value="new-spotlight">Create new spotlight</button>
                </div>
            </#if>
        </div>
    </#if>
    <@form_submit "Start quiz"></@form_submit>
</form>
<script src="/static/card_quiz_select.js"></script>