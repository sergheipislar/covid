import { handleResponse, handleError } from "./apiUtils";
const baseUrl = process.env.API_URL + "/records";

export function getRecords(regionId) {
  const finalUrl = regionId ? baseUrl + "?region.id=" + regionId : baseUrl;
  return fetch(finalUrl).then(handleResponse).catch(handleError);
}

export function getRecordsByDate(start, end) {
  const finalUrl = baseUrl + `/bydate?start=${start}&end=${end}`;
  return fetch(finalUrl).then(handleResponse).catch(handleError);
}

export function getRecordsByRegionIdsAndDate(regionIds, start, end) {
  const finalUrl =
    baseUrl +
    `/byregions?start=${start}&end=${end}&region.id=${regionIds.join(
      "&region.id="
    )}`;
  return fetch(finalUrl).then(handleResponse).catch(handleError);
}

export function getRecordsFromLastDay() {
  const finalUrl = baseUrl + "/lastday";
  return fetch(finalUrl).then(handleResponse).catch(handleError);
}

export function getRecordsGroupByDate(start, end) {
  const finalUrl = baseUrl + "/all?start=" + start + "&end=" + end;
  return fetch(finalUrl).then(handleResponse).catch(handleError);
}
