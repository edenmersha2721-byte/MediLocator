import { NavLink } from "react-router-dom";
import { toast } from "sonner";
import {
  HomeIcon,
  SearchIcon,
  FileTextIcon,
  BookmarkIcon,
  UserIcon,
  SettingsIcon,
  HeartPulseIcon,
  TruckIcon,
  ArrowRightIcon,
} from "lucide-react";
import { PATHS } from "@/router/routes";
import { cn } from "@/lib/utils";

// Active items route to real features; "soon" items are shown (to mirror the
// product vision) but disabled — no backend exists for them yet.
const NAV = [
  { to: PATHS.CUSTOMER_HOME, label: "Home", icon: HomeIcon, end: true },
  { to: PATHS.CUSTOMER_SEARCH, label: "Search Medicine", icon: SearchIcon },
  { to: PATHS.CUSTOMER_PRESCRIPTIONS, label: "Upload Prescription", icon: FileTextIcon },
];

const SOON = [
  { label: "My Reservations", icon: BookmarkIcon },
  { label: "My Profile", icon: UserIcon },
  { label: "Settings", icon: SettingsIcon },
];

function NavItem({ to, label, icon: Icon, end }) {
  return (
    <NavLink
      to={to}
      end={end}
      className={({ isActive }) =>
        cn(
          "flex items-center gap-3 rounded-xl px-3.5 py-2.5 text-sm font-medium transition-all duration-200",
          isActive
            ? "bg-gradient-to-r from-indigo-600 to-violet-600 text-white shadow-lg shadow-indigo-600/25"
            : "text-muted-foreground hover:bg-muted hover:text-foreground"
        )
      }
    >
      <Icon className="size-[18px]" />
      {label}
    </NavLink>
  );
}

/** Fixed left sidebar for the customer area (desktop). */
export default function CustomerSidebar() {
  return (
    <aside className="fixed inset-y-0 left-0 z-40 hidden w-64 flex-col border-r border-foreground/5 bg-background/80 backdrop-blur-xl lg:flex">
      {/* Brand */}
      <div className="flex items-center gap-3 px-5 py-5">
        <span className="flex size-10 items-center justify-center rounded-2xl bg-gradient-to-br from-indigo-600 to-violet-600 text-white shadow-lg shadow-indigo-600/25">
          <HeartPulseIcon className="size-5" />
        </span>
        <div className="leading-tight">
          <p className="font-heading text-base font-bold tracking-tight">Medicine Locator</p>
          <p className="text-[11px] text-muted-foreground">Find medicines. Save time.</p>
        </div>
      </div>

      {/* Nav */}
      <nav className="flex flex-1 flex-col gap-1 overflow-y-auto px-3">
        {NAV.map((item) => (
          <NavItem key={item.to} {...item} />
        ))}

        <p className="px-3.5 pb-1 pt-4 text-[11px] font-semibold uppercase tracking-wider text-muted-foreground/70">
          Coming soon
        </p>
        {SOON.map(({ label, icon: Icon }) => (
          <span
            key={label}
            className="flex cursor-not-allowed items-center gap-3 rounded-xl px-3.5 py-2.5 text-sm font-medium text-muted-foreground/50"
          >
            <Icon className="size-[18px]" />
            {label}
            <span className="ml-auto rounded-full bg-muted px-1.5 py-0.5 text-[10px] font-semibold text-muted-foreground">
              Soon
            </span>
          </span>
        ))}
      </nav>

      {/* Promo card */}
      <div className="m-3 rounded-2xl border border-foreground/5 bg-gradient-to-br from-indigo-500/10 to-violet-500/10 p-4">
        <div className="mb-2 flex size-9 items-center justify-center rounded-xl bg-white text-indigo-600 shadow-sm">
          <TruckIcon className="size-5" />
        </div>
        <p className="text-sm font-semibold text-foreground">Medicine at your doorstep</p>
        <p className="mt-0.5 text-xs text-muted-foreground">Coming soon to make life easier!</p>
        <button
          onClick={() => toast.success("Thanks! We'll notify you when delivery launches.")}
          className="mt-3 inline-flex w-full items-center justify-between rounded-xl border border-foreground/10 bg-background px-3 py-2 text-sm font-medium transition-colors hover:border-indigo-500/40 hover:text-indigo-700"
        >
          Notify Me
          <ArrowRightIcon className="size-4" />
        </button>
      </div>
    </aside>
  );
}
