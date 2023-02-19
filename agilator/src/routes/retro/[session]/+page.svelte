<script>
import Board from '../Board.svelte';
import { page } from '$app/stores';
</script>

<Board session={$page.params.session}></Board>
