import React from "react";
import { useSelector } from "react-redux";
import { RootState } from "../app/store";

export default function Schede() {
  const user = useSelector((s: RootState) => s.auth.user);

  if (!user) return <p>Devi essere loggato per accedere alle schede.</p>;

  return (
    <div>
      <h3>Schede allenamento</h3>
      <p>Piano: {user.tipoPiano}</p>
      <ul>
        {(user.tipoPiano === "PREMIUM" || user.tipoPiano === "GOLD") && <li>Schede standard + personalizzate</li>}
        {user.tipoPiano === "SILVER" && <li>Schede standard solo</li>}
        {user.tipoPiano === "FREE" && <li>Nessuna scheda disponibile per il piano FREE</li>}
      </ul>
    </div>
  );
}
