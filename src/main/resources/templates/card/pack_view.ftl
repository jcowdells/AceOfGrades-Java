<#include "/common/base.ftl">
<#include "/common/forms/input/redirect_button.ftl">
<@content>
    <div class="centre-wide">
        <div class="generic-container">
            <h3>${pack_name}</h3>
            <p>${pack_description}</p>
        </div>
        <div class="generic-container">
            <h3>Quiz</h3>
            <#include "/common/forms/card/card_quiz_select.ftl">
        </div>
        <#if is_creator>
            <div class="left-content generic-container">
                <h3>Add</h3>
                <p>Click the button to add cards to this pack.</p>
                <@redirect_button "/packs/${pack_id}/cards/create" "Add cards"></@redirect_button>
            </div>
            <div class="left-content generic-container">
                <h3>Edit</h3>
                <p>Click the button to edit the details of this pack.</p>
                <@redirect_button "/packs/${pack_id}/edit" "Edit pack"></@redirect_button><br>
                <p>Click the button to edit cards in this pack.</p>
                <@redirect_button "/packs/${pack_id}/cards/edit" "Edit cards"></@redirect_button>
            </div>
            <div class="left-content generic-container">
                <h3>Move</h3>
                <p>Click the button to move cards from this pack to another pack.</p>
                <@redirect_button "/packs/${pack_id}/cards/move/select" "Move cards"></@redirect_button>
            </div>
            <div class="left-content generic-container">
                <h3>Find cards</h3>
                <p>Click the button to find cards to add to this pack.</p>
                <@redirect_button "/explore" "Find cards"></@redirect_button>
            </div>
        </#if>
        <#if user.role != "ANYONE">
            <#if !is_creator>
                <div class="left-content generic-container">
                    <h3>Follow</h3>
                    <p>Click the button to add this pack to your collection.</p>
                    <button id="follow-button" class="form-button">
                        Follow
                    </button>
                    <script>
                        const follow_button = document.getElementById("follow-button");
                        follow_button.addEventListener("click", (event) => {
                            fetch(
                                "/forms/packs/${pack_id}/follow/", {
                                    method: "POST"
                                }
                            )
                        })
                    </script>
                </div>
            </#if>
            <div class="left-content generic-container">
                <h3>Steal</h3>
                <p>Click the button to steal cards from this pack.</p>
                <@redirect_button "/packs/${pack_id}/cards/steal/select" "Steal"></@redirect_button>
            </div>
        </#if>
        <#if is_creator>
            <div class="left-content generic-container">
                <h3>Delete</h3>
                <p>Click the button to delete cards from this pack.</p>
                <@redirect_button "/packs/${pack_id}/cards/delete" "Delete cards"></@redirect_button>
                <br>
                <h3 style="color: lightcoral">Danger zone</h3>
                <p style="color: lightcoral">Click the button to delete this pack.</p>
                <@redirect_button "/packs/${pack_id}/delete" "Delete pack"></@redirect_button>
            </div>
        </#if>
    </div>
</@content>