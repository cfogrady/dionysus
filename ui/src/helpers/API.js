import config from '../Config';

let JWT = null;

export const setJWT = jwt => JWT = jwt;

export const GET = ({path}) => {
    return fetch(`${config.apiURL}/${path}`, {
        method: "GET",
        mode: "cors",
        headers: {
            "jwt-authorization": JWT,
            "Content-Type": "application/json",
        },
    });
};

export const POST = ({ path, body }) => {
    return fetch(`${config.apiURL}/${path}`, {
        method: "POST",
        mode: "cors",
        headers: {
            "jwt-authorization": JWT,
            "Content-Type": "application/json",
        },
        body: JSON.stringify(body)
    });
};

export const PUT = ({ path, body }) => {
    return fetch(`${config.apiURL}/${path}`, {
        method: "PUT",
        mode: "cors",
        headers: {
            "jwt-authorization": JWT,
            "Content-Type": "application/json",
        },
        body: JSON.stringify(body),
    });
};