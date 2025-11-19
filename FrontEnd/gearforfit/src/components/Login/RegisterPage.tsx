import React, { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function RegisterPage() {
  const [nome, setNome] = useState("");
  const [cognome, setCognome] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // comportamento mock: qui normalmente faresti fetch POST -> /auth/register
    console.log("register:", { nome, cognome, email, password });
    // semplice validazione minima
    if (!nome || !cognome || !email || !password) {
      alert("Compila tutti i campi");
      return;
    }
    alert("Registrazione (mock) effettuata. Effettua il login.");
    navigate("/login");
  };

  return (
    <div className="card mx-auto" style={{ maxWidth: 520 }}>
      <div className="card-body">
        <h5 className="card-title">Registrazione</h5>
        <form onSubmit={handleSubmit}>
          <div className="mb-2">
            <label className="form-label">Nome</label>
            <input className="form-control" value={nome} onChange={e => setNome(e.target.value)} />
          </div>

          <div className="mb-2">
            <label className="form-label">Cognome</label>
            <input className="form-control" value={cognome} onChange={e => setCognome(e.target.value)} />
          </div>

          <div className="mb-2">
            <label className="form-label">Email</label>
            <input type="email" className="form-control" value={email} onChange={e => setEmail(e.target.value)} />
          </div>

          <div className="mb-3">
            <label className="form-label">Password</label>
            <input type="password" className="form-control" value={password} onChange={e => setPassword(e.target.value)} />
          </div>

          <button className="btn btn-primary">Registrati</button>
        </form>
      </div>
    </div>
  );
}
