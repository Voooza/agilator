<script>
 import { createEventDispatcher } from 'svelte';
 import { messageStore } from './store.js';
 import { nick } from './store.js';
 
 export let placeholder = '';
 export let color = "";
 export let bg_color = "";
 export let category = "";

 const dispatch = createEventDispatcher();
 
 let remarks = [];
 let highlightUsr = "";
 
 function add(input) {
     if (input.value.length >= 3){
         dispatch ('message', {text: input.value, cat: category});
         input.value = "";
     }
 }

 export function setHighlightUser (user){
     highlightUsr = user;
 }
 
 messageStore.subscribe (value => {
     if (value) {
         let newRemarks = [];
         for (var k of Object.keys (value.remarks)) {
             let val = value.remarks [k];
             if (val.cat === category) {
                 newRemarks.push ({"uuid": k,
                                  "cat": val.cat,
                                  "owner": val.owner,
                                  "content": val.content,
                                  "reactions": val.reactions
                                  })
             }
         }
         remarks = newRemarks;
     }
 });

 function addSpin (e){
     e.target.classList.add("la-spin");
 }
 function removeSpin (e){
     e.target.classList.remove("la-spin");
 }

 function react (uuid,kind){
     dispatch ('reaction', {uuid: uuid, kind: kind});
 }

 function deleteRemark (uuid){
     dispatch('delete', {uuid: uuid});
 }

 function rectifyReactions (reactions){
     
     let byKind = {};
     for (const r of Object.values (reactions)){
         let p = byKind [r.kind] + 1 || 1;
         byKind [r.kind] = p;
     }
     let rectified = [];
     for (const kind of Object.keys (byKind)){
         rectified.push ({kind: kind, count: byKind [kind]});
     }
     return rectified;
 }

 let reaction2icon = {
     "thumbs-up": "la-thumbs-up",
     "thumbs-down": "la-thumbs-down",
     "angry": "la-angry",
     "sad": "la-sad-tear",
     "grin": "la-grin-squint",
 }

</script>

<div class='board'>
    <input
        placeholder={placeholder}
        on:keydown={e => e.key === 'Enter' && add(e.target)}
    >
    <div>
        {#each remarks as remark}
            <label style="background-color: {bg_color};
                          color: {color}; 
                          border: 1px solid {color};
                          opacity: {highlightUsr.length > 0 && highlightUsr !== remark.owner ? 0.2 :  1};">
                <span class="reactors">
                    <i class="las la-xs la-thumbs-up"   on:mouseenter={addSpin} on:mouseleave={removeSpin} on:click={() => react (remark.uuid, "thumbs-up")}></i>
                    <i class="las la-xs la-thumbs-down" on:mouseenter={addSpin} on:mouseleave={removeSpin} on:click={() => react (remark.uuid, "thumbs-down")}></i>
                    <i class="las la-xs la-angry"       on:mouseenter={addSpin} on:mouseleave={removeSpin} on:click={() => react (remark.uuid, "angry")}></i>
                    <i class="las la-xs la-sad-tear"    on:mouseenter={addSpin} on:mouseleave={removeSpin} on:click={() => react (remark.uuid, "sad")}></i>
                    <i class="las la-xs la-grin-squint" on:mouseenter={addSpin} on:mouseleave={removeSpin} on:click={() => react (remark.uuid, "grin")}></i>
                    {#if remark.owner === $nick}
                        <i class="las la-xs la-trash" on:mouseenter={addSpin} on:mouseleave={removeSpin} on:click="{() => deleteRemark(remark.uuid)}"></i>        
                    {/if}
                </span>
                {remark.content} <br/>
                {#if Object.keys (remark.reactions).length > 0}
                    <span class="reactions">
                        {#each rectifyReactions (remark.reactions) as reaction}
                            {#if reaction.count > 1}
                                <span class="reaction-count">{reaction.count}x</span>
                            {/if}
                            <i class="las la-xs {reaction2icon[reaction.kind]}" on:mouseenter={addSpin} on:mouseleave={removeSpin} on:click={() => react (remark.uuid, reaction.kind)}></i>
                        {/each}
                    </span>
                {/if}
                
                
                
            </label>
        {/each}
    </div>
</div>

<style>
 .board {
     margin: 0 auto;
     min-width: 15em;
 }
 
 .board > input {
     font-size: 1.4em;
     /* display: block; */
     margin: 0 0 0.5em 0;
     padding: 0.5em 0.2em 0.5em 0.2em;
     line-height: 1.2;
     min-width: 15em;
     border-radius: 15px 7px;

 }

 label {
     position: relative;
     line-height: 1.2;
     font-size: 1.4em;
     padding: 0.5em 0.9em 0.5em 0.5em;
     margin: 0 0 0.5em 0;
     user-select: none;
     border-radius: 15px 7px;
     display: block;
     max-width: 14em;
     word-wrap: break-word;
 }

 span.reactors {
     opacity: 0;
     transition: opacity 0.2s;
     cursor: pointer;
     float: right;
     position: absolute;
     top: -0.5em;
     right: 0.2em;
     background-color: inherit;
     border: inherit;
     border-radius:inherit;
     padding: 0em 0.5em;
 }
 
 label:hover span.reactors {
     opacity:1;
 }

 
 span.reactions {
     opacity: 0.2;
     transition: opacity 0.2s;
     cursor: pointer;
     float: right;
     position: relative;
     top: -0.4em;
     right: 0.2em;
     background-color: inherit;
     border: inherit;
     border-radius:inherit;
     padding: 0em 0.5em;
 }
 
 label:hover span.reactions {
     opacity:1;
 }

 span.reaction-count {
     font-size: 0.5em;
     position: relative;
     top: -0.2em;
     left: 0.5em;
 }
 
</style>

