<script>
// import ky from 'ky';
import axios from 'axios';
async function getData() {
  try {
    // const response = await ky.get('/api/users');
    const response = await axios.get('/api/users');
    const data = response.data; // Access the response data directly
    return data.users;
  } catch (error) {
    return [];
  }
}

export { getData };
</script>


