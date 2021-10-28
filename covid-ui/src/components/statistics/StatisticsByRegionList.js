import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import Paper from "@material-ui/core/Paper";
import { Link } from "react-router-dom";
import PropTypes from "prop-types";

const useStyles = makeStyles({
  table: {
    minWidth: 650,
  },
});

const StatisticsByRegionList = ({ regions, records }) => {
  const classes = useStyles();

  const groupByRegion = (items) =>
    items.reduce(
      (groups, item) => ({
        ...groups,
        [item.region.id]: [...(groups[item.region.id] || []), item],
      }),
      {}
    );

  const grouped = groupByRegion(records);

  const dateColoumns = [...new Set(records.map((record) => record.date))];

  const rows =
    records.length !== 0 && Object.keys(grouped).length === regions.length
      ? regions.map((region) => {
          const values = grouped[region.id].map((record) => ({
            numberOfCases: record.numberOfCases,
            difference: record.difference,
            date: record.date,
          }));
          return { region: grouped[region.id][0].region, values };
        })
      : [];

  return (
    <TableContainer component={Paper}>
      <Table className={classes.table} size="small" aria-label="a dense table">
        <TableHead>
          <TableRow>
            <TableCell>Region</TableCell>
            {dateColoumns.map((date) => (
              <TableCell key={date} align="right">
                {date.substring(5)}
              </TableCell>
            ))}
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((row) => (
            <TableRow key={row.region.name} hover>
              <TableCell component="th" scope="row">
                <Link to={"/statistics/byregion/" + row.region.id}>
                  {row.region.name}
                </Link>
              </TableCell>
              {row.values.map(({ date, numberOfCases, difference }) => (
                <TableCell key={`${row.region.name}-${date}`} align="right">
                  {numberOfCases} / {difference}
                </TableCell>
              ))}
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

StatisticsByRegionList.propTypes = {
  records: PropTypes.array.isRequired,
  regions: PropTypes.array.isRequired,
};

export default StatisticsByRegionList;
