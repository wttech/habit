import React from 'react';
import {Link} from "@reach/router";

const isActive = ({ isCurrent }: {isCurrent: boolean}) => {
    return { className: `list-group-item list-group-item-action ${isCurrent ? 'active' : '' }` };
}

const NavLink = (props: any) => (
    <Link
        getProps={isActive}
        {...props}
    />
);

export default NavLink;