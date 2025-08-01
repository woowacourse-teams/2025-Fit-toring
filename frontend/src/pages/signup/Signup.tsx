import SignupForm from './components/SignupForm/SignupForm';
import SignupHeader from './components/SignupHeader/SignupHeader';
import SignupIntro from './components/SignupIntro/SignupIntro';

function Signup() {
  return (
    <div>
      <SignupHeader />
      <SignupIntro />
      <SignupForm />
    </div>
  );
}

export default Signup;
