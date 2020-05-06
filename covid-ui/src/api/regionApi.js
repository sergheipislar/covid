import { handleResponse, handleError } from "./apiUtils";
const baseUrl = process.env.API_URL + "/regions/";

export function getRegions() {
  return fetch(baseUrl).then(handleResponse).catch(handleError);
}
