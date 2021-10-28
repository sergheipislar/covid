import * as types from "./actionTypes";
import * as regionApi from "../../api/regionApi";
import { beginApiCall, apiCallError } from "./apiStatusActions";

export function loadRegionsSuccess(regions) {
  return { type: types.LOAD_REGIONS_SUCCESS, regions };
}

export function loadRegions() {
  return function (dispatch) {
    dispatch(beginApiCall());
    return regionApi
      .getRegions()
      .then((regions) => {
        dispatch(loadRegionsSuccess(regions));
      })
      .catch((error) => {
        dispatch(apiCallError(error));
        throw error;
      });
  };
}
