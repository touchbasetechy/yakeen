<%--
  Template: grails-app/views/taglib/_emojiPicker.gsp
  Namespace: emoji  /  Tag: <emoji:picker />

  KEY FIX: the popover HTML is built ONCE on first open.
  Category tabs, search and skin-tone updates mutate only the
  .ep-body and .ep-tab nodes in-place — Bootstrap's popover is
  never re-invoked, so it never closes.

  Requirements: Bootstrap 5 JS + CSS loaded on the page.
--%>

<%-- ═══════════════  CSS (injected once per page)  ═════════════════ --%>
<g:if test="${!request.getAttribute('emojiPickerCssInjected')}">
<%  request.setAttribute('emojiPickerCssInjected', true) %>
<style id="emoji-picker-css">
.ep-popover{
  max-width:352px!important;width:352px;
  border:1px solid rgba(255,255,255,.45)!important;
  border-radius:18px!important;
  box-shadow:0 12px 48px rgba(0,0,0,.22),0 2px 8px rgba(0,0,0,.12)!important;
  background:rgba(255,255,255,.92)!important;
  backdrop-filter:blur(28px) saturate(1.8)!important;
  -webkit-backdrop-filter:blur(28px) saturate(1.8)!important;
  overflow:hidden!important;
}
.ep-popover .popover-body{padding:0!important;background:transparent!important}
.ep-popover .popover-arrow::before,
.ep-popover .popover-arrow::after{border-top-color:rgba(255,255,255,.92)!important}
@media(prefers-color-scheme:dark){
  .ep-popover{
    background:rgba(28,28,28,.92)!important;
    border-color:rgba(255,255,255,.12)!important;
  }
  .ep-popover .popover-arrow::before,
  .ep-popover .popover-arrow::after{border-top-color:rgba(28,28,28,.92)!important}
  .ep-search{color:#eee!important}
  .ep-section-label{color:rgba(255,255,255,.35)!important}
  .ep-empty{color:#eee!important}
}
.ep-wrap{display:flex;flex-direction:column;user-select:none;-webkit-user-select:none;
  font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Helvetica,Arial,sans-serif}
.ep-search-row{display:flex;align-items:center;gap:6px;padding:10px 10px 6px}
.ep-search{flex:1;height:34px;padding:0 12px;border:none;outline:none;
  background:rgba(0,0,0,.07);border-radius:20px;font-size:13px;color:#111;transition:background .2s}
.ep-search::placeholder{color:rgba(0,0,0,.38);font-size:13px}
.ep-search:focus{background:rgba(0,168,132,.12)}
.ep-tone-wrap{position:relative;flex-shrink:0}
.ep-tone-btn{width:32px;height:32px;border:none;border-radius:50%;
  background:rgba(0,0,0,.07);cursor:pointer;font-size:18px;
  display:flex;align-items:center;justify-content:center;transition:transform .15s,background .15s}
.ep-tone-btn:hover{transform:scale(1.12);background:rgba(0,168,132,.15)}
.ep-tone-menu{position:absolute;right:0;bottom:38px;z-index:9999;
  background:rgba(255,255,255,.97);border:1px solid rgba(0,0,0,.1);
  border-radius:14px;padding:6px;display:none;gap:4px;
  box-shadow:0 4px 20px rgba(0,0,0,.18)}
.ep-tone-menu.open{display:flex}
.ep-tone-opt{width:30px;height:30px;border:none;border-radius:50%;
  background:transparent;cursor:pointer;font-size:19px;
  display:flex;align-items:center;justify-content:center;transition:transform .12s}
.ep-tone-opt:hover{transform:scale(1.25)}
.ep-tabs{display:flex;border-bottom:1px solid rgba(0,0,0,.08);
  padding:0 4px;overflow-x:auto;scrollbar-width:none;-ms-overflow-style:none}
.ep-tabs::-webkit-scrollbar{display:none}
.ep-tab{flex:1;min-width:32px;height:38px;border:none;background:transparent;
  cursor:pointer;font-size:18px;position:relative;
  display:flex;align-items:center;justify-content:center;
  opacity:.5;transition:opacity .15s}
.ep-tab:hover{opacity:.8}
.ep-tab.active{opacity:1}
.ep-tab.active::after{content:'';position:absolute;bottom:0;left:15%;right:15%;height:2.5px;
  background:var(--ep-accent,#00a884);border-radius:2px}
.ep-body{height:248px;overflow-y:auto;padding:4px 4px 8px;
  scrollbar-width:thin;scrollbar-color:rgba(0,0,0,.15) transparent}
.ep-body::-webkit-scrollbar{width:4px}
.ep-body::-webkit-scrollbar-thumb{background:rgba(0,0,0,.14);border-radius:4px}
.ep-section-label{font-size:10.5px;font-weight:700;letter-spacing:.06em;
  color:rgba(0,0,0,.4);text-transform:uppercase;padding:8px 6px 2px}
.ep-grid{display:grid;grid-template-columns:repeat(8,1fr);gap:1px}
.ep-emoji{width:38px;height:38px;border:none;background:transparent;cursor:pointer;
  font-size:22px;border-radius:8px;
  display:flex;align-items:center;justify-content:center;
  transition:background .1s,transform .1s;position:relative;z-index:0;line-height:1}
.ep-emoji:hover{background:rgba(0,168,132,.13);transform:scale(1.25);z-index:1}
.ep-emoji:active{transform:scale(.92)}
.ep-empty{display:flex;flex-direction:column;align-items:center;justify-content:center;
  height:100%;gap:8px;opacity:.4;font-size:13px;color:#111}
.ep-empty-icon{font-size:34px}
.ep-trigger-btn{background:transparent;border:none;cursor:pointer;font-size:24px;
  padding:6px;border-radius:50%;line-height:1;
  transition:transform .15s,background .15s;
  display:inline-flex;align-items:center;justify-content:center}
.ep-trigger-btn:hover{background:rgba(0,168,132,.12);transform:scale(1.1)}
</style>
</g:if>

<%-- ═══════════════  EMOJI DATA (injected once per page)  ══════════════ --%>
<g:if test="${!request.getAttribute('emojiPickerDataInjected')}">
<%  request.setAttribute('emojiPickerDataInjected', true) %>
<script id="emoji-picker-data">
window.__EP__=window.__EP__||(function(){
  const T=['','\\u{1F3FB}','\\u{1F3FC}','\\u{1F3FD}','\\u{1F3FE}','\\u{1F3FF}'];
  const S=new Set(['😀','😃','😄','😁','😆','😅','🤣','😂','🙂','🙃','😉','😊','😇','🥰','😍','🤩','😘','😗','😚','😙','🥲','😏','😒','😞','😔','😟','😕','🙁','☹','😣','😖','😫','😩','🥺','😢','😭','😤','😠','😡','🤬','🤯','😳','🥵','🥶','😱','😨','😰','😥','😓','🤗','🤔','🤭','🤫','🤥','😶','😐','😑','😬','🙄','😯','😦','😧','😮','😲','🥱','😴','🤤','😪','😵','🤐','🥴','🤢','🤮','🤧','😷','🤒','🤕','👋','🤚','🖐','✋','🖖','👌','🤌','🤏','✌','🤞','🤟','🤘','🤙','👈','👉','👆','🖕','👇','☝','👍','👎','✊','👊','🤛','🤜','👏','🙌','🫶','👐','🤲','🤝','🙏','💅','🤳','💪','🦾','🦿','🦵','🦶','👂','🦻','👃','👁','👅','🦷','💋','👶','🧒','👦','👧','🧑','👱','👨','🧔','👩','🧓','👴','👵','🏃','🚶','🧍','🧎','🧖','🧗','🏊','🏋','🚴','🤸','⛹','🏌','🏇','🤺','🤼','🤾','🏄','🏂','🤽','🚣','🧘','🛀','🛌']);
  const C=[
    {id:'recent',icon:'🕐',label:'Recently used',emojis:[]},
    {id:'smileys',icon:'😀',label:'Smileys & Emotion',emojis:['😀','😃','😄','😁','😆','😅','🤣','😂','🙂','🙃','😉','😊','😇','🥰','😍','🤩','😘','😗','😚','😙','🥲','😋','😛','😜','🤪','😝','🤑','🤗','🤭','🤫','🤔','🤐','🤨','😐','😑','😶','😏','😒','🙄','😬','🤥','😌','😔','😪','🤤','😴','😷','🤒','🤕','🤢','🤮','🤧','🥵','🥶','🥴','😵','🤯','🤠','🥳','🥸','😎','🤓','🧐','😕','🙁','☹️','😣','😖','😫','😩','🥺','😢','😭','😤','😠','😡','🤬','😈','👿','💀','☠️','💩','🤡','👹','👺','👻','👽','👾','🤖','😺','😸','😹','😻','😼','😽','🙀','😿','😾','💋','💌','💘','💝','💖','💗','💓','💞','💕','💟','❣️','💔','❤️','🧡','💛','💚','💙','💜','🖤','🤍','🤎','💯','💢','💥','💫','💦','💨','🕳️','💬','💭','💤','🔥','✨','🌟','⭐','🌈']},
    {id:'people',icon:'👋',label:'People & Body',emojis:['👋','🤚','🖐️','✋','🖖','👌','🤌','🤏','✌️','🤞','🤟','🤘','🤙','👈','👉','👆','🖕','👇','☝️','👍','👎','✊','👊','🤛','🤜','🫷','🫸','👏','🙌','🫶','👐','🤲','🤝','🙏','💅','🤳','💪','🦾','🦿','🦵','🦶','👂','🦻','👃','🫀','🫁','🧠','🦷','🦴','👀','👁️','👅','👶','🧒','👦','👧','🧑','👱','👨','🧔','👩','🧓','👴','👵','🙍','🙎','🙅','🙆','💁','🙋','🧏','🙇','🤦','🤷','💆','💇','🚶','🧍','🧎','🏃','💃','🕺','👯','🧖','🧗','🏋️','🤼','🤸','⛹️','🤺','🏇','🏊','🏄','🚣','🧘','🛀','🛌','👫','👬','👭','👨‍👩‍👦','👪','🗣️','👤','👥','🫂']},
    {id:'animals',icon:'🐶',label:'Animals & Nature',emojis:['🐶','🐱','🐭','🐹','🐰','🦊','🐻','🐼','🐻‍❄️','🐨','🐯','🦁','🐮','🐷','🐸','🐵','🙈','🙉','🙊','🐔','🐧','🐦','🐤','🦆','🦅','🦉','🦇','🐺','🐗','🐴','🦄','🐝','🪱','🐛','🦋','🐌','🐞','🐜','🪰','🪲','🪳','🦟','🦗','🕷️','🦂','🐢','🐍','🦎','🦖','🦕','🐙','🦑','🦐','🦞','🦀','🐡','🐠','🐟','🐬','🐳','🐋','🦈','🐊','🐅','🐆','🦓','🦍','🦧','🦣','🐘','🦛','🦏','🐪','🐫','🦒','🦘','🦬','🐃','🐂','🐄','🐎','🐖','🐏','🐑','🦙','🐐','🦌','🐕','🐩','🦮','🐈','🪶','🐓','🦃','🦤','🦚','🦜','🦢','🦩','🕊️','🐇','🦝','🦨','🦡','🦫','🦦','🦥','🐁','🐀','🐿️','🦔','🌵','🎄','🌲','🌳','🌴','🪵','🌱','🌿','☘️','🍀','🎍','🎋','🍃','🍂','🍁','🍄','🐚','🪸','🌾','💐','🌷','🌹','🥀','🪷','🌺','🌸','🌼','🌻','🌞','🌝','🌛','🌜','🌚','🌕','🌙','🌌','☀️','⛅','☁️','🌧️','⛈️','❄️','☃️','⛄','🌬️','💧','🌊','🌈','⚡','🔥','🌪️']},
    {id:'food',icon:'🍎',label:'Food & Drink',emojis:['🍎','🍐','🍊','🍋','🍌','🍉','🍇','🍓','🫐','🍈','🍒','🍑','🥭','🍍','🥥','🥝','🍅','🍆','🥑','🫒','🥦','🥬','🥒','🌶️','🫑','🧄','🧅','🥔','🍠','🥐','🥯','🍞','🥖','🥨','🧀','🥚','🍳','🧈','🥞','🧇','🥓','🥩','🍗','🍖','🦴','🌭','🍔','🍟','🍕','🫓','🌮','🌯','🫔','🥙','🧆','🍲','🫕','🥣','🥗','🍿','🧂','🥫','🍱','🍘','🍙','🍚','🍛','🍜','🍝','🍢','🍣','🍤','🍥','🥮','🍡','🥟','🥠','🥡','🦀','🦞','🦐','🦑','🦪','🍦','🍧','🍨','🍩','🍪','🎂','🍰','🧁','🥧','🍫','🍬','🍭','🍮','🍯','🍼','🥛','☕','🫖','🍵','🧃','🥤','🧋','🍶','🍾','🍷','🍸','🍹','🍺','🍻','🥂','🥃','🫗','🧊','🥢','🍽️','🍴','🥄','🫙','🧂']},
    {id:'travel',icon:'✈️',label:'Travel & Places',emojis:['🌍','🌎','🌏','🌐','🗺️','🧭','🏔️','⛰️','🌋','🗻','🏕️','🏖️','🏜️','🏝️','🏞️','🏟️','🏛️','🏗️','🧱','🛖','🏘️','🏚️','🏠','🏡','🏢','🏣','🏤','🏥','🏦','🏨','🏩','🏪','🏫','🏬','🏭','🏯','🏰','💒','🗼','🗽','⛪','🕌','🛕','🕍','⛩️','🕋','⛲','⛺','🌁','🌃','🏙️','🌄','🌅','🌆','🌇','🌉','🎠','🛝','🎡','🎢','💈','🎪','🚂','🚃','🚄','🚅','🚆','🚇','🚈','🚉','🚊','🚝','🚞','🚋','🚌','🚍','🚎','🚐','🚑','🚒','🚓','🚕','🚗','🚙','🛻','🚚','🚛','🚜','🏎️','🏍️','🛵','🛺','🚲','🛴','🛹','🛼','⛽','🚧','⚓','⛵','🚤','🛥️','🛳️','⛴️','🚢','✈️','🛩️','🛫','🛬','🪂','💺','🚁','🚀','🛸','🌠','⛱️']},
    {id:'activities',icon:'⚽',label:'Activities',emojis:['⚽','🏀','🏈','⚾','🥎','🎾','🏐','🏉','🎱','🏓','🏸','🏒','🥊','🥋','🥅','⛳','🎿','🛷','🥌','🎯','🪃','🏹','🎣','🤿','🎽','🎲','🎮','🎰','🧩','🧸','♟️','🖼️','🎨','🖌️','🖍️','✏️','📝','🎪','🎭','🎬','🎤','🎧','🎵','🎶','🎷','🎸','🎹','🎺','🎻','🥁','🪘','🪗','🪕','🎙️','📻','📺','🎞️','🎟️','🎫','🎗️','🎀','🎊','🎉','🎈','🎁','🎄','🎆','🎇','🧨','✨','🎍','🎑','🧧','🎋','🎃','🪄','🔮','🧿','🪬','🪩','🕹️','♠️','♥️','♦️','♣️','♟️','🃏','🎴','🀄']},
    {id:'objects',icon:'💡',label:'Objects',emojis:['⌚','📱','📲','💻','⌨️','🖥️','🖨️','🖱️','💽','💾','💿','📀','🧮','📷','📸','📹','🎥','📽️','📞','☎️','📟','📠','📺','📻','🧭','⏱️','⏲️','⏰','🕰️','⌛','⏳','📡','🔋','🪫','🔌','💡','🔦','🕯️','🪔','🧱','🔮','💈','🪄','🔑','🗝️','🔐','🔏','🔒','🔓','🗡️','⚔️','🛡️','🪃','🏹','🔧','🪛','🔩','⚙️','🗜️','🔗','⛓️','🪝','🧲','🪜','🧰','🪤','⚗️','🔭','🔬','🩺','💊','🩹','🩼','🩻','🩸','🧬','🦠','🫧','🌡️','🛁','🚿','🪥','🧴','🧷','🪢','🧹','🧺','🧻','🚽','🪣','🧼','🪒','🧽','🪮','🛒','🚪','🪞','🛏️','🛋️','🪑','📦','📫','📪','📬','📭','📮','✏️','📝','📄','📃','📋','📊','📈','📉','🗒️','🗓️','📆','📅','📇','🗃️','🗂️','🗄️','🗑️','📌','📍','📎','🖇️','📏','📐','✂️','📂','📁','📰','🗞️','📓','📔','📒','📕','📗','📘','📙','📚','🔖','🏷️','💰','🪙','💵','💶','💷','💸','💳','💹','💲','✉️','📧','📨','📩','📤','📥']},
    {id:'symbols',icon:'❤️',label:'Symbols',emojis:['❤️','🧡','💛','💚','💙','💜','🖤','🤍','🤎','♥️','💔','❣️','💕','💞','💓','💗','💖','💘','💝','💟','☮️','✝️','☪️','🕉️','☸️','✡️','🔯','🕎','☯️','☦️','🛐','⛎','♈','♉','♊','♋','♌','♍','♎','♏','♐','♑','♒','♓','🆔','⚛️','🉑','☢️','☣️','📴','📳','🈶','🈚','🈸','🈺','🈷️','✴️','🆚','💮','🉐','㊙️','㊗️','🈴','🈵','🈹','🈲','🅰️','🅱️','🆎','🆑','🅾️','🆘','❌','⭕','🛑','⛔','📛','🚫','💯','💢','♨️','🚷','🚯','🚳','🚱','🔞','📵','🚭','❗','❕','❓','❔','‼️','⁉️','🔅','🔆','〽️','⚠️','🚸','🔱','⚜️','🔰','♻️','✅','❇️','✳️','❎','🌐','💠','Ⓜ️','🌀','💤','🏧','🚾','♿','🅿️','🛗','🈳','🈂️','🛂','🛃','🛄','🛅','🚹','🚺','🚼','⚧️','🚻','🚰','🚮','🎦','📶','🈁','ℹ️','🔤','🔡','🔠','🆖','🆗','🆙','🆒','🆕','🆓','0️⃣','1️⃣','2️⃣','3️⃣','4️⃣','5️⃣','6️⃣','7️⃣','8️⃣','9️⃣','🔟','🔢','#️⃣','*️⃣','⏏️','▶️','⏸️','⏹️','⏺️','⏭️','⏮️','⏩','⏪','⏫','⏬','◀️','🔼','🔽','➡️','⬅️','⬆️','⬇️','↗️','↘️','↙️','↖️','↕️','↔️','↪️','↩️','⤴️','⤵️','🔀','🔁','🔂','🔄','🔃','➕','➖','➗','✖️','♾️','💲','💱','™️','©️','®️','〰️','➰','➿','🔚','🔙','🔛','🔝','🔜','✔️','☑️','🔘','🔴','🟠','🟡','🟢','🔵','🟣','⚫','⚪','🟤','🔺','🔻','🔷','🔶','🔹','🔸','🔲','🔳','▪️','▫️','◾','◽','◼️','◻️','🟥','🟧','🟨','🟩','🟦','🟪','⬛','⬜','🟫','🔈','🔇','🔉','🔊','📣','📢','🔔','🔕','🎵','🎶','💬','💭','🗯️','💫','💥']},
    {id:'flags',icon:'🏳️',label:'Flags',emojis:['🏳️','🏴','🏴‍☠️','🏁','🚩','🏳️‍🌈','🏳️‍⚧️','🇦🇩','🇦🇪','🇦🇫','🇦🇬','🇦🇱','🇦🇲','🇦🇴','🇦🇷','🇦🇹','🇦🇺','🇦🇿','🇧🇦','🇧🇧','🇧🇩','🇧🇪','🇧🇫','🇧🇬','🇧🇭','🇧🇮','🇧🇯','🇧🇲','🇧🇳','🇧🇴','🇧🇷','🇧🇸','🇧🇹','🇧🇼','🇧🇾','🇧🇿','🇨🇦','🇨🇩','🇨🇫','🇨🇬','🇨🇭','🇨🇮','🇨🇰','🇨🇱','🇨🇲','🇨🇳','🇨🇴','🇨🇷','🇨🇺','🇨🇻','🇨🇼','🇨🇾','🇨🇿','🇩🇪','🇩🇯','🇩🇰','🇩🇲','🇩🇴','🇩🇿','🇪🇨','🇪🇪','🇪🇬','🇪🇷','🇪🇸','🇪🇹','🇪🇺','🇫🇮','🇫🇯','🇫🇰','🇫🇲','🇫🇴','🇫🇷','🇬🇦','🇬🇧','🇬🇩','🇬🇪','🇬🇭','🇬🇮','🇬🇱','🇬🇲','🇬🇳','🇬🇶','🇬🇷','🇬🇹','🇬🇺','🇬🇼','🇬🇾','🇭🇰','🇭🇳','🇭🇷','🇭🇹','🇭🇺','🇮🇩','🇮🇪','🇮🇱','🇮🇳','🇮🇶','🇮🇷','🇮🇸','🇮🇹','🇯🇲','🇯🇴','🇯🇵','🇰🇪','🇰🇬','🇰🇭','🇰🇮','🇰🇲','🇰🇳','🇰🇵','🇰🇷','🇰🇼','🇰🇾','🇰🇿','🇱🇦','🇱🇧','🇱🇨','🇱🇮','🇱🇰','🇱🇷','🇱🇸','🇱🇹','🇱🇺','🇱🇻','🇱🇾','🇲🇦','🇲🇨','🇲🇩','🇲🇬','🇲🇰','🇲🇱','🇲🇲','🇲🇳','🇲🇴','🇲🇵','🇲🇷','🇲🇸','🇲🇹','🇲🇺','🇲🇻','🇲🇼','🇲🇽','🇲🇾','🇲🇿','🇳🇦','🇳🇪','🇳🇬','🇳🇮','🇳🇱','🇳🇴','🇳🇵','🇳🇷','🇳🇿','🇴🇲','🇵🇦','🇵🇪','🇵🇬','🇵🇭','🇵🇰','🇵🇱','🇵🇷','🇵🇸','🇵🇹','🇵🇼','🇵🇾','🇶🇦','🇷🇴','🇷🇸','🇷🇺','🇷🇼','🇸🇦','🇸🇧','🇸🇨','🇸🇩','🇸🇪','🇸🇬','🇸🇮','🇸🇰','🇸🇱','🇸🇲','🇸🇳','🇸🇴','🇸🇷','🇸🇸','🇸🇹','🇸🇻','🇸🇾','🇸🇿','🇹🇨','🇹🇩','🇹🇬','🇹🇭','🇹🇯','🇹🇰','🇹🇱','🇹🇲','🇹🇳','🇹🇴','🇹🇷','🇹🇹','🇹🇻','🇹🇼','🇹🇿','🇺🇦','🇺🇬','🇺🇸','🇺🇾','🇺🇿','🇻🇦','🇻🇨','🇻🇪','🇻🇬','🇻🇮','🇻🇳','🇻🇺','🇼🇸','🇾🇪','🇿🇦','🇿🇲','🇿🇼','🏴󠁧󠁢󠁥󠁮󠁧󠁿','🏴󠁧󠁢󠁳󠁣󠁴󠁿','🏴󠁧󠁢󠁷󠁬󠁳󠁿']},
  ];
  return {C,T,S};
})();
</script>
</g:if>

<%-- ═══════════════  TRIGGER BUTTON  ════════════════════════════════ --%>
<button
  id="${uniqueId}"
  type="button"
  class="ep-trigger-btn ${buttonClass}"
  style="--ep-accent:${accentColor}"
  aria-label="${buttonLabel}"
  aria-haspopup="true"
  aria-expanded="false"
  data-ep-target="${targetInput}"
>😊</button>

<%-- ═══════════════  INIT SCRIPT  ═══════════════════════════════════ --%>
<script>
(function(){
  var BID='${uniqueId}';
  var TARGET='${targetInput}';
  var LS_KEY='ep_recent_v2';
  var MAX_REC=32;
  var btnEl=document.getElementById(BID);
  if(!btnEl||typeof bootstrap==='undefined')return;

  var D=window.__EP__;
  if(!D){console.warn('EmojiPicker: data not loaded');return;}
  var CATS=D.C, T=D.T, S=D.S;

  /* ── state ── */
  var st={cat:'smileys',search:'',tone:0};

  /* ── recent ── */
  function getR(){try{return JSON.parse(localStorage.getItem(LS_KEY)||'[]');}catch(e){return[];}}
  function saveR(a){try{localStorage.setItem(LS_KEY,JSON.stringify(a.slice(0,MAX_REC)));}catch(e){}}
  function addR(e){var r=getR();r=r.filter(function(x){return x!==e;});r.unshift(e);saveR(r);}

  /* ── skin tone ── */
  function wt(e){
    if(st.tone===0)return e;
    var b=e.replace(/[\u{1F3FB}-\u{1F3FF}]/gu,'');
    return S.has(b)?b+T[st.tone]:e;
  }

  /* ── insert into target ── */
  function ins(em){
    if(!TARGET)return;
    var el=document.querySelector(TARGET);
    if(!el)return;
    if(el.tagName==='INPUT'||el.tagName==='TEXTAREA'){
      var s=el.selectionStart||0,en=el.selectionEnd||0,v=el.value;
      el.value=v.slice(0,s)+em+v.slice(en);
      el.selectionStart=el.selectionEnd=s+em.length;
      el.focus();
    } else if(el.isContentEditable){
      el.focus();
      var sel=window.getSelection();
      if(sel&&sel.rangeCount){
        var r=sel.getRangeAt(0);r.deleteContents();
        r.insertNode(document.createTextNode(em));r.collapse(false);
      } else {el.textContent+=em;}
    }
    el.dispatchEvent(new Event('input',{bubbles:true}));
  }

  /* ── render tabs (in-place, no popover rebuild) ── */
  function rTabs(wrap){
    wrap.querySelectorAll('.ep-tab').forEach(function(b){
      var a=b.dataset.cat===st.cat;
      b.classList.toggle('active',a);
      b.setAttribute('aria-selected',a);
    });
  }

  /* ── render grid (in-place, no popover rebuild) ── */
  function rGrid(wrap){
    CATS[0].emojis=getR();
    var sq=st.search.trim().toLowerCase();
    var body=wrap.querySelector('.ep-body');
    if(!body)return;
    var html='',any=false;
    for(var ci=0;ci<CATS.length;ci++){
      var cat=CATS[ci];
      if(cat.id==='recent'&&cat.emojis.length===0)continue;
      if(!sq&&cat.id!==st.cat)continue;
      var grid='',cnt=0;
      for(var ei=0;ei<cat.emojis.length;ei++){
        var em=wt(cat.emojis[ei]);
        if(!em||!em.trim())continue;
        grid+='<button class="ep-emoji" type="button" data-emoji="'+em+'">'+em+'</button>';
        cnt++;
      }
      if(cnt>0){
        var lbl=(sq||cat.id==='recent')?'<div class="ep-section-label">'+cat.label+'</div>':'';
        html+=lbl+'<div class="ep-grid">'+grid+'</div>';
        any=true;
      }
    }
    if(!any)html='<div class="ep-empty"><div class="ep-empty-icon">🔍</div><div>No emojis found</div></div>';
    body.innerHTML=html;
    body.scrollTop=0;
  }

  /* ── render tone button face ── */
  var TFACE=['👋','👋🏻','👋🏼','👋🏽','👋🏾','👋🏿'];
  function rTone(wrap){
    var b=wrap.querySelector('.ep-tone-btn');
    if(b)b.textContent=TFACE[st.tone];
  }

  /* ── build initial popover HTML (called once by Bootstrap) ── */
  function buildHTML(){
    CATS[0].emojis=getR();
    var tabs='<div class="ep-tabs" role="tablist">';
    for(var i=0;i<CATS.length;i++){
      var c=CATS[i];
      if(c.id==='recent'&&c.emojis.length===0)continue;
      var a=c.id===st.cat;
      tabs+='<button class="ep-tab'+(a?' active':'')+'" role="tab" aria-selected="'+a+'" data-cat="'+c.id+'" title="'+c.label+'" type="button">'+c.icon+'</button>';
    }
    tabs+='</div>';

    var tm='<div class="ep-tone-wrap"><button class="ep-tone-btn" type="button" title="Skin tone">'+TFACE[st.tone]+'</button><div class="ep-tone-menu">';
    for(var ti=0;ti<TFACE.length;ti++)tm+='<button class="ep-tone-opt" data-tone="'+ti+'" type="button">'+TFACE[ti]+'</button>';
    tm+='</div></div>';

    return '<div class="ep-wrap" style="--ep-accent:${accentColor}">'+
      '<div class="ep-search-row"><input class="ep-search" type="search" placeholder="Search emoji\u2026" autocomplete="off" />'+tm+'</div>'+
      tabs+'<div class="ep-body"></div></div>';
  }

  /* ── attach all events ONCE after popover is shown ── */
  function attach(wrap){
    /* tabs — mutate state, re-render grid only */
    wrap.querySelectorAll('.ep-tab').forEach(function(b){
      b.addEventListener('click',function(e){
        e.stopPropagation();
        st.cat=b.dataset.cat;
        st.search='';
        var si=wrap.querySelector('.ep-search');
        if(si)si.value='';
        rTabs(wrap);
        rGrid(wrap);
      });
    });

    /* emoji grid — delegated, survives innerHTML swap */
    var body=wrap.querySelector('.ep-body');
    body.addEventListener('click',function(e){
      var btn=e.target.closest('.ep-emoji');
      if(!btn)return;
      var em=btn.dataset.emoji;
      addR(em);
      ins(em);
      btn.style.background='rgba(0,168,132,.28)';
      setTimeout(function(){btn.style.background='';},180);
    });

    /* search */
    var si=wrap.querySelector('.ep-search');
    if(si){
      si.addEventListener('input',function(){
        st.search=si.value;
        rGrid(wrap);
      });
      setTimeout(function(){si.focus();},80);
    }

    /* tone */
    var tb=wrap.querySelector('.ep-tone-btn');
    var tm=wrap.querySelector('.ep-tone-menu');
    if(tb&&tm){
      tb.addEventListener('click',function(e){e.stopPropagation();tm.classList.toggle('open');});
      tm.querySelectorAll('.ep-tone-opt').forEach(function(o){
        o.addEventListener('click',function(e){
          e.stopPropagation();
          st.tone=parseInt(o.dataset.tone);
          tm.classList.remove('open');
          rTone(wrap);
          rGrid(wrap);
        });
      });
      document.addEventListener('click',function(){tm.classList.remove('open');});
    }

    /* first render */
    rGrid(wrap);
  }

  /* ── Bootstrap popover (manual trigger so WE control open/close) ── */
  var pop=new bootstrap.Popover(btnEl,{
    html:true,
    sanitize:false,
    content:buildHTML,
    placement:'${placement}',
    customClass:'ep-popover',
    trigger:'manual',
  });

  btnEl.addEventListener('click',function(e){
    e.stopPropagation();
    pop.toggle();
  });

  btnEl.addEventListener('shown.bs.popover',function(){
    btnEl.setAttribute('aria-expanded','true');
    var pb=document.querySelector('.ep-popover .popover-body');
    if(pb)attach(pb);
  });

  btnEl.addEventListener('hidden.bs.popover',function(){
    btnEl.setAttribute('aria-expanded','false');
    st.search='';
  });

  /* close on outside click — skip if click is inside the popover */
  document.addEventListener('click',function(e){
    var po=document.querySelector('.ep-popover');
    if(po&&po.contains(e.target))return;
    if(btnEl.contains(e.target))return;
    pop.hide();
  });
})();
</script>
