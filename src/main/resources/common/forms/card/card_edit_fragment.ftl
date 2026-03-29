<#include "card_editor.ftl">
<div class="centre-wide" style="height:100%; background: radial-gradient(
    circle,
    rgba(0, 0, 0, 0.5) 40%,
    rgba(0, 0, 0, 0) 90%
  ); filter: blur(5px); margin: 5px; pointer-events: all;">
    <div class="left-content generic-container">
        <form hx-post="/forms/cards/${card_id}/edit" hx-target="this" hx-swap="outerHTML">
            <@card_editor "Update card"></@card_editor>
        </form>
    </div>
   <div style="width: 10%">
       <button id="edit-close" class="form-button">Close</button>
   </div>
</div>
<script src="/static/card_create.js"></script>