import * as types from "./actionTypes";
import * as recordApi from "../../api/recordApi";
import { beginApiCall, apiCallError } from "./apiStatusActions";

export function loadRecordsSuccess(records) {
  return { type: types.LOAD_RECORDS_SUCCESS, records };
}

export function loadGroupedRecordsSuccess(groupedRecords) {
  return { type: types.LOAD_GROUPED_RECORDS_SUCCESS, groupedRecords };
}

export function loadRecords(regionId) {
  return function (dispatch) {
    dispatch(beginApiCall());
    return recordApi
      .getRecords(regionId)
      .then((records) => {
        dispatch(loadRecordsSuccess(records));
      })
      .catch((error) => {
        dispatch(apiCallError(error));
        throw error;
      });
  };
}

export function loadRecordsByDate(start, end) {
  return function (dispatch) {
    dispatch(beginApiCall());
    return recordApi
      .getRecordsByDate(start, end)
      .then((records) => {
        dispatch(loadRecordsSuccess(records));
      })
      .catch((error) => {
        dispatch(apiCallError(error));
        throw error;
      });
  };
}

export function loadRecordsByRegionIdsAndDate(regionIds, start, end) {
  return function (dispatch) {
    dispatch(beginApiCall());
    return recordApi
      .getRecordsByRegionIdsAndDate(regionIds, start, end)
      .then((records) => {
        dispatch(loadRecordsSuccess(records));
      })
      .catch((error) => {
        dispatch(apiCallError(error));
        throw error;
      });
  };
}

export function loadRecordsGroupByDate(start, end) {
  return function (dispatch) {
    dispatch(beginApiCall());
    return recordApi
      .getRecordsGroupByDate(start, end)
      .then((groupedRecords) => {
        dispatch(loadGroupedRecordsSuccess(groupedRecords));
      })
      .catch((error) => {
        dispatch(apiCallError(error));
        throw error;
      });
  };
}
