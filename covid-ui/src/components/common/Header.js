import React from "react";
import { NavLink } from "react-router-dom";

const Header = () => {
  const activeStyle = { color: "#F15B2A" };
  return (
    <nav>
      <NavLink to="/" activeStyle={activeStyle} exact>
        Dashboard
      </NavLink>
      {" | "}
      <NavLink to="/statistics" activeStyle={activeStyle} exact>
        All
      </NavLink>
      {" | "}
      <NavLink to="/statistics/byregions" activeStyle={activeStyle} exact>
        By regions
      </NavLink>
      {" | "}
      <NavLink to="/statistics/byregion" activeStyle={activeStyle} exact>
        By region
      </NavLink>
    </nav>
  );
};

export default Header;
