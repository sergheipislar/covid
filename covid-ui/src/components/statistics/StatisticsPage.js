import React, { useEffect, useState } from "react";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import { connect } from "react-redux";
import * as regionActions from "../../redux/actions/regionActions";
import * as recordActions from "../../redux/actions/recordActions";
import PropTypes from "prop-types";
import { bindActionCreators } from "redux";
import TextField from "@material-ui/core/TextField";
import StatisticsByRegionList from "./StatisticsByRegionList";
import StatisticsAll from "./StatisticsAll";
import StatisticsRegionPage from "./StatisticsRegionPage";
import { calculateTodayDateWithOffsetInSqlFormat } from "./Utils";

const StatisticsPage = ({
  regions,
  records,
  groupedRecords,
  actions,
  location,
}) => {
  const [start, setStart] = useState(
    calculateTodayDateWithOffsetInSqlFormat(-7)
  );
  const [end, setEnd] = useState(calculateTodayDateWithOffsetInSqlFormat(0));

  let { path } = useRouteMatch();

  useEffect(() => {
    if (regions.length === 0) {
      actions.loadRegions().catch((error) => {
        alert("Loading regions failed " + error);
      });
    }
  });

  useEffect(() => {
    if (location.pathname.indexOf("regions") !== -1) {
      actions.loadRecordsByDate(start, end).catch((error) => {
        alert("Loading records failed " + error);
      });
    } else {
      actions.loadRecordsGroupByDate(start, end).catch((error) => {
        alert("Loading records failed " + error);
      });
    }
  }, [start, end, location.pathname]);

  const handleDateChange = (event) => {
    const { id, value } = event.target;
    if (id === "start") {
      setStart(value);
    } else if (id === "end") {
      setEnd(value);
    }
  };

  return (
    <div style={{ marginTop: "10px" }}>
      <form noValidate>
        <TextField
          id="start"
          label="Start"
          type="date"
          value={start}
          InputLabelProps={{
            shrink: true,
          }}
          onChange={handleDateChange}
        />
        <TextField
          id="end"
          label="End"
          type="date"
          value={end}
          InputLabelProps={{
            shrink: true,
          }}
          onChange={handleDateChange}
        />
        <div style={{ marginTop: "20px" }}>
          <Switch>
            <Route exact path={path}>
              <StatisticsAll records={groupedRecords} />
            </Route>
            <Route path={`${path}/byregions`}>
              <StatisticsByRegionList regions={regions} records={records} />
            </Route>
            <Route path={`${path}/byregion/:regionId`}>
              <StatisticsRegionPage start={start} end={end} />
            </Route>
            <Route path={`${path}/byregion`}>
              <StatisticsRegionPage start={start} end={end} />
            </Route>
          </Switch>
        </div>
      </form>
    </div>
  );
};

StatisticsPage.propTypes = {
  regions: PropTypes.array.isRequired,
  records: PropTypes.array.isRequired,
  groupedRecords: PropTypes.array.isRequired,
  actions: PropTypes.object.isRequired,
  location: PropTypes.object.isRequired,
};

function mapStateToProps(state) {
  return {
    regions: state.regions,
    records: state.records,
    groupedRecords: state.groupedRecords,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      loadRecordsGroupByDate: bindActionCreators(
        recordActions.loadRecordsGroupByDate,
        dispatch
      ),
      loadRecordsByDate: bindActionCreators(
        recordActions.loadRecordsByDate,
        dispatch
      ),
      loadRegions: bindActionCreators(regionActions.loadRegions, dispatch),
    },
  };
}

export default connect(mapStateToProps, mapDispatchToProps)(StatisticsPage);
