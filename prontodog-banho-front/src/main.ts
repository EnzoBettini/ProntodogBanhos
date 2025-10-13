import './assets/styles/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

// Font Awesome
import { library } from '@fortawesome/fontawesome-svg-core'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import {
  faHome,
  faDog,
  faUsers,
  faChevronDown,
  faChevronUp,
  faCalendarDays,
  faScissors,
  faBath,
  faBars,
  faXmark,
  faPlus,
  faUserPlus,
  faChartBar,
  faCog,
  faPalette,
  faArrowLeft,
  faCogs,
  faBell,
  faUser,
  faSignOutAlt,
  faSpinner,
  faRefresh,
  faExclamationTriangle,
  faSearch,
  faPhone,
  faEnvelope,
  faExclamationCircle,
  faCheckCircle,
  faSave,
  faTimes,
  faTrash,
  faIdCard,
  faHashtag,
  faAt,
  faEye,
  faEdit
} from '@fortawesome/free-solid-svg-icons'

library.add(
  faHome,
  faDog,
  faUsers,
  faChevronDown,
  faChevronUp,
  faCalendarDays,
  faScissors,
  faBath,
  faBars,
  faXmark,
  faPlus,
  faUserPlus,
  faChartBar,
  faCog,
  faPalette,
  faArrowLeft,
  faCogs,
  faBell,
  faUser,
  faSignOutAlt,
  faSpinner,
  faRefresh,
  faExclamationTriangle,
  faSearch,
  faPhone,
  faEnvelope,
  faExclamationCircle,
  faCheckCircle,
  faSave,
  faTimes,
  faTrash,
  faIdCard,
  faHashtag,
  faAt,
  faEye,
  faEdit,
  faTrash,
  faChartBar,
  faCalendarDays
)

const app = createApp(App)

app.component('FontAwesomeIcon', FontAwesomeIcon)
app.use(createPinia())
app.use(router)

app.mount('#app')
