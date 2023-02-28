<script>
 import Column from './Column.svelte';
 import Users from './Users.svelte';
 import { nick } from './store.js';
 import { sendMessage } from "./store.js";
 
 export let session;

 let tmpNick = "";

 let wwwCol;
 let wthCol;
 let wwsCol;
 let wtfCol;
 
 function announce () {
     let msg = JSON.stringify ({
         app: "retro",
         nick: $nick,
         session: session
     });
     window.setTimeout (() => sendMessage (msg), 1000);
 }
 
 function go (){
     if (tmpNick.length >= 3) {
         nick.set(tmpNick);
         announce ();
     }
 }
 
 function handleMessage (event){
     sendMessage (JSON.stringify ({
         app: "retro",
         nick: $nick,
         session: session,
         remark: {
             owner: $nick,
             cat: event.detail.cat,
             content: event.detail.text
         }
     }));
 }

 
 function handleDelete (event){
     sendMessage (JSON.stringify ({
         app: "retro",
         nick: $nick,
         session: session,
         delete: {
             uuid:event.detail.uuid
         }
     }));
 }

 function handleReaction (event){
     sendMessage (JSON.stringify ({
         app: "retro",
         nick: $nick,
         session: session,
         react: {
             nick: $nick,
             uuid: event.detail.uuid,
             kind: event.detail.kind
         }
     }));
 }
 
function handleUserMouseEnter (msg) {
                                     wwwCol.setHighlightUser (msg.detail.name);
                                     wthCol.setHighlightUser (msg.detail.name);
                                     wwsCol.setHighlightUser (msg.detail.name);
                                     wtfCol.setHighlightUser (msg.detail.name);
                                     }

 function handleUserMouseLeave (msg) {
     wwwCol.setHighlightUser ("");
     wthCol.setHighlightUser ("");
     wwsCol.setHighlightUser ("");
     wtfCol.setHighlightUser ("");
 }
 
 function handleUserClick (msg) {
     if (msg.detail.name === $nick){
         nick.set ("");
     }
 }

 function goExport (){
     let loc = window.location;
     window.location.href = loc.origin + loc.pathname.slice (0,-11) + "/export" + loc.search;
 }

 let loc = window.location;
 let exportUrl = loc.origin + loc.pathname.slice (0,-11) + "/export" + loc.search;
 if ($nick.length > 2) announce ();
</script>

{#if $nick.length > 2} 
    <Users on:mouseenter={handleUserMouseEnter} on:mouseleave={handleUserMouseLeave} on:click={handleUserClick}></Users>
    <div class='board'>
        <Column bind:this={wwwCol} category="www" placeholder="What went well?" color="#2ECC71" bg_color="#D5F5E3" on:message={handleMessage} on:reaction={handleReaction} on:delete={handleDelete}></Column>
        <Column bind:this={wthCol} category="wth" placeholder="What the hell?"  color="#922B21" bg_color="#F2D7D5" on:message={handleMessage} on:reaction={handleReaction} on:delete={handleDelete}></Column>
        <Column bind:this={wwsCol} category="wws" placeholder="What we shall?"  color="#1F618D" bg_color="#D4E6F1" on:message={handleMessage} on:reaction={handleReaction} on:delete={handleDelete}></Column>
        <Column bind:this={wtfCol} category="wtf" placeholder="WTF?"            color="#CA6F1E" bg_color="#FAE5D3" on:message={handleMessage} on:reaction={handleReaction} on:delete={handleDelete}></Column>
    </div>
    <a class="export_link" href="{exportUrl}" target="_blank">export</a>
    

    
{:else}
    <div class="form">
        <input class="inp" name="" type="text" bind:value={tmpNick}
               placeholder="Nick name... at least 3 characters"
               on:keydown={e => e.key === 'Enter' && go()}/>
    </div>
{/if}

<style>
 
 .board {
     display: grid;
     grid-template-columns: auto;
     grid-gap: 10px;
     margin: 0 auto;
     max-width: 1440px;
 }

 @media (min-width: 720px) {
     .board { grid-template-columns: repeat(2, auto); }
 }
 
 @media (min-width: 1440px) {
     .board { grid-template-columns: repeat(4, auto); }
 }
 
 .form {
     display: block;
     margin: 10em auto;
     width: 50%;
     border-radius: 15px 7px;
     padding: 10px;
 }

 .inp {
     width: 95%;
     border-radius: 15px 7px;
     font-size: 1.4em;
     padding: 1em;
 }
 .export_link {
     text-decoration: none;
     margin: 3em;
 }
</style>
