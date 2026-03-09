<div id="modal">

</div>
<div class="centre-content" style="width: 100%; height: 100%">
    <div id="game-container"
        data-pack-id="${pack_id}"
    >
        <div id="card-container">
            <div id="card-next">
                Next card
            </div>
            <div id="card">
                <div id="card-front" class="prevent-select" style="background: white">
                    legendmixer FRONT
                </div>
                <div id="card-back" class="prevent-select" style="background: white">
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
    <div class="generic-container centre-content">
        <button id="edit-button" class="form-button" hx-get="/forms/">Edit card</button>
    </div>
</div>