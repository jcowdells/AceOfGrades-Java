const cards_container = document.getElementById("cards-container");

let selected_cards = new Set();
let last_clicked = null;

cards_container.querySelectorAll(".card").forEach(element => {
    if (element.dataset.selected === "true") selected_cards.add(element.dataset.cardId);
})

cards_container.addEventListener("click", (event) => {
    const card = event.target.closest(".card");
    if (!card) return;

    function selectCard(card) {
        const id = card.dataset.cardId;
        if (selected_cards.has(id)) {
            card.dataset.selected = "false";
            selected_cards.delete(id);
        } else {
            card.dataset.selected = "true";
            selected_cards.add(id);
        }
    }

    if (event.shiftKey && last_clicked) {
        const cards = Array.from(cards_container.querySelectorAll(".card"));
        let start_index, end_index = 0;
        for (let i = 0; i < cards.length; i++) {
            const card_id = cards[i].dataset.cardId;
            if (card_id === last_clicked) start_index = i;
            if (card_id === card.dataset.cardId) end_index = i;
        }
        if (start_index > end_index) {
            const tmp = end_index;
            end_index = start_index;
            start_index = tmp;
        }
        cards.slice(start_index, end_index + 1).forEach(i_card => {
            if (i_card.dataset.cardId !== last_clicked) selectCard(i_card);
        });
    } else {
        selectCard(card);
        if (card.dataset.selected === "false") last_clicked = null;
        else last_clicked = card.dataset.cardId;
    }
});