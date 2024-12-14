export function setSession(user) {
  if (user) {
    sessionStorage.setItem('username', user['username']);
    sessionStorage.setItem('sessionId', user['sessionId']);
  } else {
    sessionStorage.setItem('username', '');
    sessionStorage.setItem('sessionId', '');
  }
}

export function getUsername() {
  if (sessionStorage.getItem('username')) {
    return sessionStorage.getItem('username');
  }
  return null;
}


export function getSession() {
  if (sessionStorage.getItem('sessionId')) {
    return sessionStorage.getItem('sessionId');
  }
  return null;
}

