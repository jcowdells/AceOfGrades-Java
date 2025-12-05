<#include "/common/base.ftl">
<@content>
    <div id="game-container">
        <div id="card-container">
            <div id="card-next">
                Next card
            </div>
            <div id="card">
                <div id="card-front" style="background: white">
                    legendmixer FRONT
                </div>
                <div id="card-back" style="background: white">
                    legendmixer BACK
                </div>
            </div>
        </div>
        <div id="stack-container">
            <div id="stack-correct">
                Correct
            </div>
            <div id="stack-incorrect">
                Incorrect
            </div>
        </div>
    </div>
    <script src="/static/card.js"></script>
</@content>