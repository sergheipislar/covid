import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { connect } from "react-redux";
import * as regionActions from "../../redux/actions/regionActions";
import * as recordActions from "../../redux/actions/recordActions";
import PropTypes from "prop-types";
import { bindActionCreators } from "redux";
import Input from "@material-ui/core/Input";
import InputLabel from "@material-ui/core/InputLabel";
import MenuItem from "@material-ui/core/MenuItem";
import FormControl from "@material-ui/core/FormControl";
import ListItemText from "@material-ui/core/ListItemText";
import Select from "@material-ui/core/Select";
import Checkbox from "@material-ui/core/Checkbox";
import { Chart } from "react-charts";

const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
  PaperProps: {
    style: {
      maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
      width: 250,
    },
  },
};

const StatisticsRegionPage = ({ regions, records, start, end, actions }) => {
  const params = useParams();
  const [regionIds, setRegionIds] = useState([
    parseInt(params.regionId || "7"),
  ]);

  useEffect(() => {
    if (regions.length === 0) {
      actions.loadRegions().catch((error) => {
        alert("Loading regions failed " + error);
      });
    }
  });

  useEffect(() => {
    actions
      .loadRecordsByRegionIdsAndDate(regionIds, start, end)
      .catch((error) => {
        alert("Loading records failed " + error);
      });
  }, [regionIds, start, end]);

  const regionsMap = regions.reduce((groups, region) => {
    return { ...groups, [region.id]: region };
  }, {});

  const convertSqlDateToJavascriptDate = (sqlDate) => {
    const [year, month, day] = sqlDate.split("-");
    return new Date(parseInt(year), parseInt(month) - 1, parseInt(day));
  };

  const data = React.useMemo(
    () =>
      regionIds.map((regionId) => ({
        label: regionsMap[regionId] ? regionsMap[regionId].name : "",
        datums: records
          .filter((record) => record.region.id === regionId)
          .map((record) => ({
            x: convertSqlDateToJavascriptDate(record.date),
            y: record.numberOfCases,
            difference: record.difference,
            regionName: record.region.name,
          })),
      })),
    [records]
  );

  const axes = React.useMemo(
    () => [
      { primary: true, type: "time", position: "bottom" },
      { type: "linear", position: "left" },
    ],
    [records]
  );

  const onRegionChange = (event) => {
    setRegionIds(event.target.value);
  };

  // eslint-disable-next-line react/prop-types
  const CustomTooltip = ({ datum }) => {
    return datum ? (
      <div>
        <h2 style={{ textAlign: "center", fontSize: "1.2rem" }}>
          {datum.originalDatum.x.toLocaleDateString("ro-RO")}
        </h2>
        {datum.group
          .sort((e1, e2) => e2.originalDatum.y - e1.originalDatum.y)
          .map((entry) => (
            <div
              key={entry.originalDatum.regionName}
              style={{
                display: "flex",
                fontSize: "1.1rem",
                alignItems: "center",
              }}
            >
              <svg width="16" height="16">
                <circle
                  cx="8"
                  cy="8"
                  r="7"
                  style={{
                    stroke: "white",
                    fill: entry.style.fill,
                    strokeWidth:
                      datum.originalDatum.regionName ===
                      entry.originalDatum.regionName
                        ? 3
                        : 2,
                  }}
                ></circle>
              </svg>
              <div
                style={{
                  marginLeft: "5px",
                  fontWeight:
                    datum.originalDatum.regionName ===
                    entry.originalDatum.regionName
                      ? "bold"
                      : "normal",
                }}
              >
                {entry.originalDatum.regionName} - ({entry.originalDatum.y}/
                {entry.originalDatum.difference > 0 ? "+" : ""}
                {entry.originalDatum.difference})
              </div>
            </div>
          ))}
      </div>
    ) : (
      ""
    );
  };

  const tooltip = React.useMemo(
    () => ({
      // eslint-disable-next-line
      render: ({ datum }) => {
        return <CustomTooltip {...{ datum }} />;
      },
    }),
    []
  );

  return (
    <div>
      <FormControl style={{ minWidth: "300px" }}>
        <InputLabel id="demo-mutiple-checkbox-label">Region</InputLabel>
        <Select
          labelId="demo-mutiple-checkbox-label"
          id="demo-mutiple-checkbox"
          multiple
          value={regionIds}
          onChange={onRegionChange}
          input={<Input />}
          renderValue={(selected) =>
            selected
              .map((sel) => (regionsMap[sel] ? regionsMap[sel].name : sel))
              .join(", ")
          }
          MenuProps={MenuProps}
        >
          {regions.map((region) => (
            <MenuItem key={region.name} value={region.id}>
              <Checkbox checked={regionIds.indexOf(region.id) > -1} />
              <ListItemText primary={region.name} />
            </MenuItem>
          ))}
        </Select>
      </FormControl>
      {records.length !== 0 ? (
        <div style={{ width: "800px", height: "500px", marginTop: "10px" }}>
          <Chart
            data={data}
            axes={axes}
            tooltip={tooltip}
            primaryCursor
            secondaryCursor
          />
        </div>
      ) : (
        ""
      )}
    </div>
  );
};

StatisticsRegionPage.propTypes = {
  regions: PropTypes.array.isRequired,
  records: PropTypes.array.isRequired,
  start: PropTypes.string.isRequired,
  end: PropTypes.string.isRequired,
  actions: PropTypes.object.isRequired,
};

function mapStateToProps(state) {
  return {
    regions: state.regions,
    records: state.records,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      loadRecordsByRegionIdsAndDate: bindActionCreators(
        recordActions.loadRecordsByRegionIdsAndDate,
        dispatch
      ),
      loadRegions: bindActionCreators(regionActions.loadRegions, dispatch),
    },
  };
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(StatisticsRegionPage);
