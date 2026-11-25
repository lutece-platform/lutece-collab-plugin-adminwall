/*
 * Recipients of a new post: "to all" by default, or a selection of 1 to n admin users picked with the autocomplete.
 * Each picked user becomes a chip holding a hidden "recipient" input.
 */
const section = document.getElementById( 'adminwall-recipients' );
const list = document.getElementById( 'adminwall-recipient-list' );
const search = document.getElementById( 'adminwall-recipient-search' );
const modeInputs = document.querySelectorAll( 'input[name="recipientMode"]' );

function isSelectedMode( ) {
    const checked = document.querySelector( 'input[name="recipientMode"]:checked' );
    return checked !== null && checked.value === 'selected';
}

function toggleSection( ) {
    const selected = isSelectedMode( );
    section.classList.toggle( 'd-none', !selected );
    // the chips are only submitted when the post goes to a selection
    list.querySelectorAll( 'input[name="recipient"]' ).forEach( input => { input.disabled = !selected; } );
}

function clearSearch( ) {
    const input = search.querySelector( '.lutece-autocomplete-search-input' );
    input.value = '';
    search.querySelector( '.lutece-autocomplete-remove' ).classList.add( 'd-none' );
}

function addRecipient( id, name ) {
    if ( list.querySelector( `input[name="recipient"][value="${CSS.escape( String( id ) )}"]` ) ) {
        return;
    }
    const chip = document.createElement( 'span' );
    chip.className = 'badge bg-blue-lt d-inline-flex align-items-center gap-1 p-2';
    chip.append( document.createTextNode( name ) );

    const hidden = document.createElement( 'input' );
    hidden.type = 'hidden';
    hidden.name = 'recipient';
    hidden.value = id;

    const remove = document.createElement( 'button' );
    remove.type = 'button';
    remove.className = 'btn-close btn-close-sm ms-1';
    remove.setAttribute( 'aria-label', `${list.dataset.removeLabel} ${name}` );
    remove.addEventListener( 'click', ( ) => chip.remove( ) );

    chip.append( hidden, remove );
    list.append( chip );
}

// reached from the "New message" button of the notification menu: open the form at once
const openNewPost = document.getElementById( 'adminwall-open-new-post' );
const newPostButton = document.querySelector( '[data-bs-toggle="modal"][data-bs-target="#createPostModal"]' );
if ( openNewPost && newPostButton ) {
    newPostButton.click( );
}

if ( section && list && search ) {
    modeInputs.forEach( input => input.addEventListener( 'change', toggleSection ) );
    search.addEventListener( 'autocomplete:select', ( event ) => {
        const suggestion = event.detail.suggestion;
        addRecipient( suggestion.id, suggestion.name );
        clearSearch( );
    } );
    toggleSection( );
}
