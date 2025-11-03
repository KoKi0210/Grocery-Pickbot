import React from "react";

type RoleSelectorProps = {
    selectedRole: string;
    onSelectRole: (role: string) => void;
}

const RoleSelector: React.FC<RoleSelectorProps> = ({ selectedRole, onSelectRole }) => {
    return (
        <div>
            <label>
                <input
                    type="radio"
                    value="USER"
                    checked={selectedRole === "USER"}
                    onChange={() => onSelectRole("USER")}
                />
                User
            </label>
            <label>
                <input
                    type="radio"
                    value="ADMIN"
                    checked={selectedRole === "ADMIN"}
                    onChange={() => onSelectRole("ADMIN")}
                />
                Admin
            </label>
        </div>
    );
}
export default RoleSelector;