<#include "/common/base.ftl">
<@content>
    <div class="centre-wide" style="height:100%;">
        <div class="arrange-containers-across">
            <div class="left-content pack-display">
                <label>Adding to pack:</label>
                <div class="pack-thumbnail" style="background: ${form.getFrontColor().input}">
                    <h3>${pack_name}</h3>
                </div>
                <div class="pack-thumbnail" style="background: ${form.getBackColor().input}">
                    <h4>${pack_description}</h4>
                </div>
            </div>
            <div class="left-content generic-container">
                <#include "/common/forms/create_card.ftl">
            </div>
        </div>
    </div>
    <script src="/static/create_card.js"></script>
</@content>