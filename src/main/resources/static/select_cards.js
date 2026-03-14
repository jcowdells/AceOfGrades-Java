let selected_cards = new Set();

document.getElementById("cards-container").addEventListener("click", (event) => {
    const card = event.target.closest(".card");
    if (!card) return;

    const id = card.dataset.cardId;
    if (selected_cards.has(id)) {
        card.dataset.selected = "false";
        selected_cards.delete(id);
    } else {
        card.dataset.selected = "true";
        selected_cards.add(id);
    }
});