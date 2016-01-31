/*
 * Suggests the article titles.
 */
function TitleSuggestions(title_list, link_list, section_list, user_id) {
    this.titles = title_list;
    this.links = link_list;
    this.sections = section_list;
    this.userId = user_id;
}

/**
 * Request suggestions for the given autosuggest control.
 * @scope protected
 * @param oAutoSuggestControl The autosuggest control to provide suggestions for.
 */
TitleSuggestions.prototype.requestSuggestions = function (oAutoSuggestControl /*:AutoSuggestControl*/,
                                                          bTypeAhead /*:boolean*/) {
    var aSuggestions = [];
    var aLinks = [];
    var aSections = [];
    var sTextboxValue = oAutoSuggestControl.textbox.value;

    if (sTextboxValue.length > 0){

        //search for matching titles
        for (var i=0; i < this.titles.length; i++) {
            if (this.titles[i].toLowerCase().indexOf
                    (sTextboxValue.toLowerCase()) != -1) {
                aSuggestions.push(this.titles[i]);
                aLinks.push(this.links[i]);
                aSections.push(this.sections[i]);
            }
        }
    }

    //provide suggestions to the control and disable type ahead.
    oAutoSuggestControl.autosuggest(aSuggestions, aLinks, aSections, this.userId, false);
};

